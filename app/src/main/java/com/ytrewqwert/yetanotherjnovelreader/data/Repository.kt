package com.ytrewqwert.yetanotherjnovelreader.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.text.Html
import android.text.Spanned
import com.ytrewqwert.yetanotherjnovelreader.data.local.LocalRepository
import com.ytrewqwert.yetanotherjnovelreader.data.local.PreferenceStore
import com.ytrewqwert.yetanotherjnovelreader.data.local.UnknownPartsProgress
import com.ytrewqwert.yetanotherjnovelreader.data.remote.RemoteRepository
import java.time.Instant
import java.time.Period

class Repository private constructor(appContext: Context) {
    companion object {
        @Volatile
        private var INSTANCE: Repository? = null
        fun getInstance() = INSTANCE
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Repository(context.applicationContext).also {
                    INSTANCE = it
                }
            }
    }

    private val prefStore: PreferenceStore = PreferenceStore.getInstance(appContext)
    private val local: LocalRepository = LocalRepository.getInstance()
    private val remote: RemoteRepository

    val fontSize: Int get() = prefStore.fontSize
    val fontStyle: Typeface get() = prefStore.fontStyle
    val readerMargin: Int get() = prefStore.readerMargin

    init {
        remote = RemoteRepository.getInstance(appContext, prefStore.authToken)
    }

    suspend fun getImage(source: String?): Bitmap? {
        if (source == null) return null
        return remote.getImage(source)
    }

    suspend fun getPart(partId: String): Spanned? {
        refreshLoginIfAuthExpired()
        val contentJson = remote.getPartContentJson(partId)
        val partHtml = contentJson?.getString("dataHTML") ?: return null
        return Html.fromHtml(partHtml, 0)
    }

    fun getPartProgress(partId: String) = local.getPart(partId)?.progress ?: 0.0

    suspend fun getRecentParts(): List<Part> {
        val oneMonthAgo = Instant.now().minus(Period.ofDays(30))
        val partsJson = remote.getPartsJsonAfter(oneMonthAgo) ?: return emptyList()
        // Funnel through LocalRepository and back so that part progress is attached to parts
        local.addData(partsJson)
        val partIds = ArrayList<String>()
        for (i in 0 until partsJson.length()) {
            partIds.add(partsJson.getJSONObject(i).getString("id"))
        }
        return local.getParts(partIds)
    }

    suspend fun getSeries(): List<Series> {
        val seriesJson = remote.getSeriesJson()
        if (seriesJson != null) local.addData(seriesJson)
        return local.getSeries()
    }

    suspend fun getSerieVolumes(seriesId: String): List<Volume> {
        val volumesJson = remote.getSerieVolumesJson(seriesId)
        if (volumesJson != null) local.addData(volumesJson)
        return local.getVolumes(seriesId)
    }

    fun getUsername() = prefStore.username

    suspend fun getVolumeParts(volume: Volume): List<Part> {
        val partsJson = remote.getVolumePartsJson(volume.id)
        if (partsJson != null) local.addData(partsJson)
        return local.getParts(volume.id)
    }

    fun isMember() = prefStore.isMember

    suspend fun login(email: String, password: String): Boolean {
        val loginJson = remote.login(email, password)
        prefStore.setUserData(loginJson)

        val userId = prefStore.userId
        if (userId != null) {
            prefStore.email = email
            prefStore.password = password
            val partProgress = remote.getUserPartProgressJson(userId)
            if (partProgress != null) local.setPartsProgress(UnknownPartsProgress(partProgress))
        }
        return loginJson != null
    }
    fun loggedIn() = (prefStore.authToken != null)
    suspend fun logout(): Boolean {
        remote.logout()
        // Don't care whether the user was actually logged out or not. Just clear data.
        prefStore.clearUserData()
        prefStore.email = null
        prefStore.password = null
        return true
    }

    suspend fun setPartProgress(partId: String, progress: Double) {
        refreshLoginIfAuthExpired()
        local.setPartProgress(partId, progress)
        val userId = prefStore.userId
        if (userId != null) remote.setUserPartProgress(userId, partId, progress)
    }

    suspend fun fetchPartProgress(): Boolean {
        refreshLoginIfAuthExpired()
        val userId = prefStore.userId ?: return false
        val partProgress = remote.getUserPartProgressJson(userId) ?: return false
        local.setPartsProgress(UnknownPartsProgress(partProgress))
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
        return if (loggedIn() && prefStore.authExpired()) {
            remote.logout()
            loginFromStore()
        } else false
    }
}