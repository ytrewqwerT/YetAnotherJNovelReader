package com.example.yetanotherjnovelreader.data

import android.content.Context
import android.graphics.Bitmap
import android.text.Html
import android.text.Spanned
import com.example.yetanotherjnovelreader.data.local.LocalRepository
import com.example.yetanotherjnovelreader.data.local.UnknownPartsProgress
import com.example.yetanotherjnovelreader.data.remote.RemoteRepository
import java.time.Instant
import java.time.Period

class Repository private constructor(appContext: Context) {
    companion object {
        @Volatile
        private var INSTANCE: Repository? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Repository(context.applicationContext).also {
                    INSTANCE = it
                }
            }
    }

    private val local = LocalRepository.getInstance(appContext.getSharedPreferences(
        "com.example.yetanotherjnovelreader.GLOBAL_PREFERENCES", Context.MODE_PRIVATE
    ))
    private val remote: RemoteRepository

    init {
        remote = RemoteRepository.getInstance(appContext, local.authToken)

        val userId = local.userId
        if (userId != null) {
            remote.getUserPartProgressJson(userId) {
                if (it != null) local.setPartsProgress(UnknownPartsProgress(it))
            }
        }
    }

    fun login(email: String, password: String, callback: (Boolean) -> Unit) {
        remote.login(email, password) { loginJson ->
            local.userId = loginJson?.getString("userId")
            local.authToken = loginJson?.getString("id")
            local.authDate = loginJson?.getString("created")
            local.username = loginJson?.getJSONObject("user")?.getString("username")

            val userId = local.userId
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
            if (logoutSuccessful) {
                local.userId = null
                local.authToken = null
                local.authDate = null
                local.username = null
            }
            callback(logoutSuccessful)
        }

    }
    fun loggedIn() = (local.authToken != null)
    fun getUsername() = local.username


    fun getSeries(callback: (List<Series>) -> Unit) {
        if (local.getSeries().isEmpty()) {
            remote.getSeriesJson {
                local.addSeriesInfo(it)
                callback(local.getSeries())
            }
        } else {
            callback(local.getSeries())
        }
    }

    fun getSerieVolumes(serie: Series, callback: (List<Volume>) -> Unit) {
        if (local.getVolumes(serie.id).isEmpty()) {
            remote.getSerieJson(serie.id) {
                local.addSerieInfo(it)
                callback(local.getVolumes(serie.id))
            }
        } else {
            callback(local.getVolumes(serie.id))
        }
    }

    fun getVolumeParts(volume: Volume, callback: (List<Part>) -> Unit) {
        if (local.getParts(volume.id).isEmpty()) {
            remote.getSerieJson(volume.serieId) {
                local.addSerieInfo(it)
                callback(local.getParts(volume.id))
            }
        } else {
            callback(local.getParts(volume.id))
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
        val userId = local.userId
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
            local.addPartsInfo(it)
            val partIds = ArrayList<String>()
            for (i in 0 until it.length()) {
                partIds.add(it.getJSONObject(i).getString("id"))
            }
            callback(local.getParts(partIds))
        }
    }
}