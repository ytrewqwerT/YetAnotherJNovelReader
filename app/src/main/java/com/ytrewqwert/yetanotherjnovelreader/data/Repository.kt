package com.ytrewqwert.yetanotherjnovelreader.data

import android.content.Context
import android.graphics.Bitmap
import android.text.Html
import android.text.Spanned
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.*
import com.ytrewqwert.yetanotherjnovelreader.data.local.preferences.PreferenceStore
import com.ytrewqwert.yetanotherjnovelreader.data.remote.RemoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.Period

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

    val horizontalReader get() = prefStore.horizontalReader
    val fontSize get() = prefStore.fontSize
    val fontStyle get() = prefStore.fontStyle
    val readerMargin get() = prefStore.readerMargin

    suspend fun getImage(source: String): Bitmap? = remote.getImage(source)
    suspend fun getPartContent(partId: String): Spanned? {
        refreshLoginIfAuthExpired()
        val partHtml = remote.getPartContentJson(partId) ?: return null
        return Html.fromHtml(partHtml, 0)
    }

    fun getSeries(scope: CoroutineScope): Flow<List<Serie>> {
        scope.launch {
            val series = remote.getSeriesJson() ?: return@launch
            local.insertSeries(*series.toTypedArray())
        }
        return local.getSeries()
    }
    fun getSerieVolumes(scope: CoroutineScope, serieId: String): Flow<List<Volume>> {
        scope.launch {
            val volumes = remote.getSerieVolumesJson(serieId) ?: return@launch
            local.insertVolumes(*volumes.toTypedArray())
        }
        return local.getSerieVolumes(serieId)
    }
    fun getVolumeParts(scope: CoroutineScope, volumeId: String): Flow<List<PartWithProgress>> {
        scope.launch {
            val parts = remote.getVolumePartsJson(volumeId) ?: return@launch
            local.insertParts(*parts.toTypedArray())
        }
        return local.getVolumeParts(volumeId)
    }
    fun getRecentParts(scope: CoroutineScope): Flow<List<PartWithProgress>> {
        val oneMonthAgo = Instant.now().minus(Period.ofDays(30))
        scope.launch {
            val parts = remote.getPartsJsonAfter(oneMonthAgo) ?: return@launch
            local.insertParts(*parts.toTypedArray())
        }
        return local.getPartsSince("$oneMonthAgo")
    }

    suspend fun getParts(vararg partId: String): List<PartWithProgress> = local.getParts(*partId)

    fun getUsername() = prefStore.username
    fun isMember() = prefStore.isMember ?: false

    suspend fun login(email: String, password: String): Boolean {
        val userData = remote.login(email, password)
        prefStore.setUserData(userData)

        // Save email/password if login successful
        val userId = prefStore.userId
        if (userId != null) {
            prefStore.email = email
            prefStore.password = password
            fetchPartProgress()
        }
        return userData != null
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
        val boundedProgress = when {
            progress > 1.0 -> 1.0
            progress < 0.0 -> 0.0
            else -> progress
        }
        local.insertProgress(Progress(partId, boundedProgress))

        val userId = prefStore.userId
        if (userId != null) remote.setUserPartProgress(userId, partId, boundedProgress)
    }
    suspend fun fetchPartProgress(): Boolean {
        refreshLoginIfAuthExpired()
        val userId = prefStore.userId ?: return false
        val progresses = remote.getUserPartProgressJson(userId) ?: return false
        local.insertProgress(*progresses.toTypedArray())
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