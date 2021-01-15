package com.ytrewqwert.yetanotherjnovelreader.data

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.work.*
import com.ytrewqwert.yetanotherjnovelreader.ProgressUploadWorker
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.LocalRepository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.follow.Follow
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.progress.Progress
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.SerieFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.preferences.PreferenceStore
import com.ytrewqwert.yetanotherjnovelreader.data.local.preferences.ReaderPreferenceStore
import com.ytrewqwert.yetanotherjnovelreader.data.remote.RemoteRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
    private val readerPrefStore = ReaderPreferenceStore.getInstance(appContext)
    private val local = LocalRepository.getInstance(appContext)
    private val remote = RemoteRepository.getInstance(appContext, prefStore.authToken)
    private val workManager = WorkManager.getInstance(appContext)

    /** Identifies whether lists should filter items to only show followed items. */
    val isFilterFollowing get() = prefStore.isFilterFollowing
    /** Set whether lists should filter items to only show followed items. */
    fun setIsFilterFollowing(value: Boolean) { prefStore.setIsFilterFollowing(value) }

    fun getReaderSettingsFlow() = readerPrefStore.readerSettingsFlow
    fun getReaderSettings() = readerPrefStore.readerSettings

    suspend fun getImage(source: String): Drawable? = remote.getImage(source)
    suspend fun getPartContent(partId: String): String? {
        refreshLoginIfAuthExpired()
        return remote.getPartHtml(partId)
    }

    fun getSerieFlow(serieId: String): Flow<SerieFull> = local.getSerie(serieId)
    fun getVolumeFlow(volumeId: String): Flow<VolumeFull> = local.getVolume(volumeId)

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
    suspend fun fetchUpNextParts(): FetchResult {
        val follows = local.getAllFollows()
        val pairs = follows.map { Pair(it.serieId, it.nextPartNum) }
        val parts = remote.getUpNextParts(pairs)
        local.upsertParts(*parts.toTypedArray())
        return FetchResult.PART_PAGE // No more "pages" since the function doesn't paginate fetches
    }

    suspend fun getVolumes(vararg volumeId: String): List<VolumeFull> = local.getVolumes(*volumeId)
    suspend fun getParts(vararg partId: String): List<PartFull> = local.getParts(*partId)

    // TODO: Handle failures to sync new follows/unfollows with remote.
    /** Returns true if the series with ID [serieId] is being followed by the user. */
    suspend fun isFollowed(serieId: String): Boolean =
        local.getAllFollows().find { it.serieId == serieId } != null
    /** Sets a series with ID [serieId] as being followed by the user. */
    suspend fun followSeries(serieId: String) {
        val userId = prefStore.userId
        if (userId != null) remote.followSerie(userId, serieId)
        val latestFinishedPart = local.getLatestFinishedPart(serieId)
        // Default the "up-next" part to the part after the latest finished part on initial follow.
        val nextPartNum = (latestFinishedPart?.part?.seriesPartNum ?: 0) + 1
        local.upsertFollows(Follow(serieId, nextPartNum))

    }
    // Note that Room database deletions are based on primary key only. '0' has no effect here.
    /** Sets a series with ID [serieId] as not being followed by the user. */
    suspend fun unfollowSeries(serieId: String) {
        val userId = prefStore.userId
        if (userId != null) remote.unfollowSerie(userId, serieId)
        local.deleteFollows(Follow(serieId, 0))
    }

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
            fetchFollowedSeries()
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

    suspend fun fetchPartsProgress(): Boolean {
        refreshLoginIfAuthExpired()
        val userId = prefStore.userId ?: return false
        val progresses = remote.getUserPartProgress(userId) ?: return false
        local.upsertProgress(*progresses.toTypedArray())
        return true
    }
    suspend fun setPartProgress(partId: String, progress: Double) {
        val boundedProgress = progress.coerceIn(0.0, 1.0)
        local.upsertProgress(Progress(partId, boundedProgress, true))
        if (boundedProgress == 1.0) {
            // Update the "up-next" part if the series is being followed. Note that
            // Room's Update functions only update rows and won't insert any new rows.
            local.getParts(partId).getOrNull(0)?.let {
                val serieId = it.part.serieId
                val nextPartNum = it.part.seriesPartNum + 1
                local.updateFollows(Follow(serieId, nextPartNum))
            }
        }
        enqueueProgressUploadWorker()
    }
    suspend fun pushProgressPendingUploadToRemote(): Boolean = coroutineScope {
        refreshLoginIfAuthExpired()
        val pendingProgress = local.getProgressPendingUpload()
        val jobs = pendingProgress.map {
            async { setRemotePartProgress(it.partId, it.progress) }
        }
        jobs.awaitAll().contains(false).not()
    }

    suspend fun fetchFollowedSeries(): Boolean {
        refreshLoginIfAuthExpired()
        val userId = prefStore.userId ?: return false
        val localFollows = local.getAllFollows()
        val remoteFollows = remote.getUserSerieFollows(userId) ?: return false

        for (newId in remoteFollows) {
            if (localFollows.find { it.serieId == newId } == null) {
                // Duplicated code with Repository.followSeries
                val latestFinishedPart = local.getLatestFinishedPart(newId)
                val nextPartNum = (latestFinishedPart?.part?.seriesPartNum ?: 0) + 1
                local.upsertFollows(Follow(newId, nextPartNum))
            }
        }
        for (oldFollow in localFollows) {
            if (!remoteFollows.contains(oldFollow.serieId)) local.deleteFollows(oldFollow)
        }

        return true
    }

    private fun enqueueProgressUploadWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val workRequest = OneTimeWorkRequestBuilder<ProgressUploadWorker>()
            .setConstraints(constraints)
            .build()
        workManager.enqueueUniqueWork(
            "PROGRESS_UPLOAD", ExistingWorkPolicy.REPLACE, workRequest
        )
    }
    private suspend fun setRemotePartProgress(partId: String, progress: Double): Boolean {
        val boundedProgress = progress.coerceIn(0.0, 1.0)
        val userId = prefStore.userId ?: return true
        val progressSet = remote.setUserPartProgress(userId, partId, boundedProgress)
        if (progressSet) {
            local.upsertProgress(Progress(partId, boundedProgress, false))
        }
        return progressSet
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