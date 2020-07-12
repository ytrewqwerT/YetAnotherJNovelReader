package com.ytrewqwert.yetanotherjnovelreader.data

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spanned
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.PartHtmlParser
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.LocalRepository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.follow.Follow
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.progress.Progress
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.SerieFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.preferences.PreferenceStore
import com.ytrewqwert.yetanotherjnovelreader.data.remote.RemoteRepository
import kotlinx.coroutines.flow.Flow

/** A one-stop shop for objects needing to interact with the locally and remotely saved data. */
class Repository private constructor(appContext: Context) {
    companion object {
        @Volatile
        private var INSTANCE: Repository? = null
        fun getInstance() = INSTANCE
        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Repository(context.applicationContext).also { INSTANCE = it }
        }
    }

    private val prefStore = PreferenceStore.getInstance(appContext)
    private val local = LocalRepository.getInstance(appContext)
    private val remote = RemoteRepository.getInstance(appContext, prefStore.authToken)

    /** Identifies whether lists should filter items to only show followed items. */
    val isFilterFollowing get() = prefStore.isFilterFollowing
    /** Set whether lists should filter items to only show followed items. */
    fun setIsFilterFollowing(value: Boolean) { prefStore.setIsFilterFollowing(value) }

    fun getReaderSettingsFlow() = prefStore.readerSettings

    suspend fun getImage(source: String): Drawable? = remote.getImage(source)
    suspend fun getPartContent(partId: String): Spanned? {
        refreshLoginIfAuthExpired()
        val partHtml = remote.getPartHtml(partId) ?: return null
        return PartHtmlParser.parse(partHtml, partId)
    }

    fun getSeriesFlow(): Flow<List<SerieFull>> = local.getSeries()
    suspend fun fetchSeries(amount: Int, offset: Int, followedOnly: Boolean): FetchResult? {
        val follows = if (followedOnly) local.getAllFollows().map { it.serieId} else null
        val series = remote.getSeries(amount, offset, follows) ?: return null
        local.upsertSeries(*series.toTypedArray())
        return if (series.size == amount) FetchResult.FULL_PAGE
        else FetchResult.PART_PAGE
    }

    fun getSerieVolumesFlow(serieId: String): Flow<List<VolumeFull>> = local.getSerieVolumes(serieId)
    suspend fun fetchSerieVolumes(serieId: String, amount: Int, offset: Int): FetchResult? {
        val volumes = remote.getSerieVolumes(serieId, amount, offset) ?: return null
        local.upsertVolumes(*volumes.toTypedArray())
        return if (volumes.size == amount) FetchResult.FULL_PAGE
        else FetchResult.PART_PAGE
    }

    fun getVolumePartsFlow(volumeId: String): Flow<List<PartFull>> = local.getVolumeParts(volumeId)
    suspend fun fetchVolumeParts(volumeId: String, amount: Int, offset: Int): FetchResult? {
        val parts = remote.getVolumeParts(volumeId, amount, offset) ?: return null
        local.upsertParts(*parts.toTypedArray())
        return if (parts.size == amount) FetchResult.FULL_PAGE
        else FetchResult.PART_PAGE
    }

    fun getRecentPartsFlow(): Flow<List<PartFull>> = local.getRecentParts()
    suspend fun fetchRecentParts(amount: Int, offset: Int, followedOnly: Boolean): FetchResult? {
        val follows = if (followedOnly) local.getAllFollows().map { it.serieId } else null
        val parts = remote.getRecentParts(amount, offset, follows) ?: return null
        local.upsertParts(*parts.toTypedArray())
        return if (parts.size == amount) FetchResult.FULL_PAGE
        else FetchResult.PART_PAGE
    }

    fun getUpNextPartsFlow(): Flow<List<PartFull>> = local.getUpNextParts()
    suspend fun fetchUpNextParts(): FetchResult? {
        val follows = local.getAllFollows()
        val pairs = follows.map { Pair(it.serieId, it.nextPartNum) }
        val parts = remote.getUpNextParts(pairs)
        local.upsertParts(*parts.toTypedArray())
        return FetchResult.PART_PAGE // No more "pages" since the function doesn't paginate fetches
    }

    suspend fun getParts(vararg partId: String): List<PartFull> = local.getParts(*partId)

    /** Sets a series with ID [serieId] as being followed by the user. */
    suspend fun followSeries(serieId: String) {
        val latestFinishedPart = local.getLatestFinishedPart(serieId)
        // Default the "up-next" part to the part after the latest finished part on initial follow.
        val nextPartNum = (latestFinishedPart?.part?.seriesPartNum ?: 0) + 1
        local.upsertFollows(Follow(serieId, nextPartNum))
    }
    // Note that Room database deletions are based on primary key only. '0' has no effect here.
    /** Sets a series with ID [serieId] as not being followed by the user. */
    suspend fun unfollowSeries(serieId: String) { local.deleteFollows(Follow(serieId, 0)) }

    fun getUsername() = prefStore.username
    fun isMember() = prefStore.isMember ?: false

    fun isLoggedIn() = (prefStore.authToken != null)
    suspend fun login(email: String, password: String): Boolean {
        val userData = remote.login(email, password)
        prefStore.setUserData(userData)

        // Save email/password if login successful
        val userId = prefStore.userId
        if (userId != null) {
            prefStore.email = email
            prefStore.password = password
            fetchPartsProgress()
        }
        return userData != null
    }
    suspend fun logout(): Boolean {
        remote.logout()
        // Don't care whether the user was actually logged out or not. Just clear data.
        prefStore.clearUserData()
        prefStore.email = null
        prefStore.password = null
        return true
    }

    suspend fun setPartProgress(partId: String, progress: Double): Boolean {
        refreshLoginIfAuthExpired()
        val boundedProgress = when {
            progress > 1.0 -> 1.0
            progress < 0.0 -> 0.0
            else -> progress
        }
        local.upsertProgress(Progress(partId, boundedProgress))
        if (boundedProgress == 1.0) {
            // Update the "up-next" part if the series is being followed. Note that
            // Room's Update functions only update rows and won't insert any new rows.
            local.getParts(partId).getOrNull(0)?.let {
                val serieId = it.part.serieId
                val nextPartNum = it.part.seriesPartNum + 1
                local.updateFollows(Follow(serieId, nextPartNum))
            }
        }

        val userId = prefStore.userId ?: return false
        return remote.setUserPartProgress(userId, partId, boundedProgress)
    }
    /**
     * Retrieves all of the user's part progress data and saves it in the local database.
     *
     * @return true if successful and false otherwise.
     */
    private suspend fun fetchPartsProgress(): Boolean {
        refreshLoginIfAuthExpired()
        val userId = prefStore.userId ?: return false
        val progresses = remote.getUserPartProgress(userId) ?: return false
        local.upsertProgress(*progresses.toTypedArray())
        return true
    }

    private suspend fun loginFromStore(): Boolean {
        val email = prefStore.email
        val password = prefStore.password
        return if (email != null && password != null) {
            val loggedIn = login(email, password)
            if (!loggedIn) {
                prefStore.email = null
                prefStore.password = null
            }
            loggedIn
        } else {
            false
        }
    }
    private suspend fun refreshLoginIfAuthExpired(): Boolean {
        return if (isLoggedIn() && prefStore.isAuthExpired()) {
            remote.logout()
            loginFromStore()
        } else false
    }
}