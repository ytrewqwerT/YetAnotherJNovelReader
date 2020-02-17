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
        fun getInstance(context: Context) =
            INSTANCE
                ?: synchronized(this) {
                INSTANCE
                    ?: Repository(
                        context.applicationContext
                    ).also {
                    INSTANCE = it
                }
            }
    }

    private val prefStore: PreferenceStore = PreferenceStore.getInstance(appContext)
    private val local: LocalRepository = LocalRepository.getInstance()
    private val remote: RemoteRepository

    val fontSize: Int get() = prefStore.fontSize
    val fontStyle: Typeface get() = prefStore.fontStyle

    init {
        remote = RemoteRepository.getInstance(appContext, prefStore.authToken)

        val userId = prefStore.userId
        if (userId != null) {
            remote.getUserPartProgressJson(userId) {
                if (it != null) local.setPartsProgress(
                    UnknownPartsProgress(
                        it
                    )
                )
            }
        }
    }

    fun login(email: String, password: String, callback: (Boolean) -> Unit) {
        remote.login(email, password) { loginJson ->
            prefStore.setUserData(loginJson)

            val userId = prefStore.userId
            if (userId != null) {
                remote.getUserPartProgressJson(userId) {
                    if (it != null) local.setPartsProgress(UnknownPartsProgress(it))
                }
            }

            callback(loginJson != null)
        }
    }
    fun logout(callback: (Boolean) -> Unit) {
        remote.logout { logoutSuccessful ->
            if (logoutSuccessful) prefStore.clearUserData()
            callback(logoutSuccessful)
        }

    }
    fun loggedIn() = (prefStore.authToken != null)
    fun getUsername() = prefStore.username

    fun getSeries(callback: (List<Series>) -> Unit) {
        if (local.getSeries().isEmpty()) {
            remote.getSeriesJson {
                local.addData(it)
                callback(local.getSeries())
            }
        } else {
            callback(local.getSeries())
        }
    }

    fun getSerieVolumes(series: Series, callback: (List<Volume>) -> Unit) =
        getSerieVolumes(series.id, callback)
    fun getSerieVolumes(seriesId: String, callback: (List<Volume>) -> Unit) {
        if (local.getVolumes(seriesId).isNotEmpty()) {
            callback(local.getVolumes(seriesId))
        } else {
            remote.getSerieJson(seriesId) {
                local.addData(it)
                callback(local.getVolumes(seriesId))
            }
        }
    }

    fun getVolumeParts(volume: Volume, callback: (List<Part>) -> Unit) {
        if (local.getParts(volume.id).isNotEmpty()) {
            callback(local.getParts(volume.id))
        } else {
            remote.getSerieJson(volume.serieId) {
                local.addData(it)
                callback(local.getParts(volume.id))
            }
        }
    }

    fun getPart(part: Part, callback: (Spanned?) -> Unit) { getPart(part.id, callback) }
    fun getPart(partId: String, callback: (Spanned?) -> Unit) {
        remote.getPartJson(partId) {
            val partHtml = it?.getString("dataHTML")
            if (partHtml != null) {
                callback(Html.fromHtml(partHtml, 0))
            } else {
                callback(null)
            }
        }
    }

    fun getPartProgress(partId: String) = local.getPart(partId)?.progress ?: 0.0
    fun setPartProgress(partId: String, progress: Double) {
        local.getPart(partId)?.progress = progress
        val userId = prefStore.userId
        if (userId != null) remote.setUserPartProgress(userId, partId, progress)
    }

    fun getImage(source: String?, callback: (Bitmap?) -> Unit) {
        if (source != null) {
            remote.getImage(source, callback)
        } else {
            callback(null)
        }
    }

    fun getRecentParts(callback: (List<Part>) -> Unit) {
        val oneMonthAgo = Instant.now().minus(Period.ofDays(30))
        remote.getPartsJsonAfter(oneMonthAgo) {
            // Funnel through LocalRepository and back so that part progress is attached to parts
            local.addData(it)
            val partIds = ArrayList<String>()
            for (i in 0 until it.length())
                partIds.add(it.getJSONObject(i).getString("id"))
            callback(local.getParts(partIds))
        }
    }
}