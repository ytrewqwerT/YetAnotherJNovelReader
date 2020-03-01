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

    fun getImage(source: String?, callback: (Bitmap?) -> Unit) {
        if (source != null) remote.getImage(source, callback)
        else callback(null)
    }

    fun getPart(partId: String, callback: (Spanned?) -> Unit) {
        refreshLoginIfAuthExpired {
            remote.getPartContentJson(partId) {
                val partHtml = it?.getString("dataHTML")
                if (partHtml != null) callback(Html.fromHtml(partHtml, 0))
                else callback(null)
            }
        }
    }

    fun getPartProgress(partId: String) = local.getPart(partId)?.progress ?: 0.0

    fun getRecentParts(callback: (List<Part>) -> Unit) {
        val oneMonthAgo = Instant.now().minus(Period.ofDays(30))
        remote.getPartsJsonAfter(oneMonthAgo) {
            // Funnel through LocalRepository and back so that part progress is attached to parts
            local.addData(it)
            val partIds = ArrayList<String>()
            for (i in 0 until it.length()) partIds.add(it.getJSONObject(i).getString("id"))
            callback(local.getParts(partIds))
        }
    }

    fun getSeries(callback: (List<Series>) -> Unit) {
        remote.getSeriesJson {
            local.addData(it)
            callback(local.getSeries())
        }
    }
    fun getSerieVolumes(seriesId: String, callback: (List<Volume>) -> Unit) {
        remote.getSerieVolumesJson(seriesId) {
            local.addData(it)
            callback(local.getVolumes(seriesId))
        }
    }

    fun getUsername() = prefStore.username

    fun getVolumeParts(volume: Volume, callback: (List<Part>) -> Unit) {
        remote.getVolumePartsJson(volume.id) {
            local.addData(it)
            callback(local.getParts(volume.id))
        }
    }

    fun isMember() = prefStore.isMember

    fun login(email: String, password: String, callback: (loggedIn: Boolean) -> Unit) {
        remote.login(email, password) { loginJson ->
            prefStore.setUserData(loginJson)

            val userId = prefStore.userId
            if (userId != null) {
                prefStore.email = email
                prefStore.password = password
                remote.getUserPartProgressJson(userId) {
                    if (it != null) local.setPartsProgress(UnknownPartsProgress(it))
                }
            }
            callback(loginJson != null)
        }
    }
    fun loggedIn() = (prefStore.authToken != null)
    fun logout(callback: (loggedOut: Boolean) -> Unit) {
        remote.logout {
            // Don't care whether the user was actually logged out or not. Just clear data.
            prefStore.clearUserData()
            prefStore.email = null
            prefStore.password = null
            callback(true)
        }
    }

    fun setPartProgress(partId: String, progress: Double) {
        refreshLoginIfAuthExpired {
            local.setPartProgress(partId, progress)
            val userId = prefStore.userId
            if (userId != null) remote.setUserPartProgress(userId, partId, progress)
        }
    }

    fun fetchPartProgress(onComplete: (fetchSuccess: Boolean) -> Unit) {
        refreshLoginIfAuthExpired {
            val userId = prefStore.userId
            if (userId == null) {
                onComplete(false)
            } else {
                remote.getUserPartProgressJson(userId) {
                    if (it == null) onComplete(false)
                    else {
                        local.setPartsProgress(UnknownPartsProgress(it))
                        onComplete(true)
                    }
                }
            }
        }
    }

    private fun loginFromStore(callback: (loggedIn: Boolean) -> Unit) {
        val email = prefStore.email
        val password = prefStore.password
        if (email != null && password != null) {
            login(email, password) {
                if (!it) {
                    prefStore.email = null
                    prefStore.password = null
                }
                callback(it)
            }
        } else {
            callback(false)
        }
    }
    private fun refreshLoginIfAuthExpired(onComplete: (refreshed: Boolean) -> Unit) {
        if (prefStore.authExpired()) remote.logout { loginFromStore(onComplete) }
        else onComplete(false)
    }
}