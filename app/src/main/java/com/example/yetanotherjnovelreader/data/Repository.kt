package com.example.yetanotherjnovelreader.data

import android.content.Context
import android.text.Html
import android.text.Spanned
import com.android.volley.toolbox.ImageLoader

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
    val imageLoader: ImageLoader get() = remote.imageLoader

    init {
        remote = RemoteRepository.getInstance(appContext, local.authToken)
    }

    fun login(email: String, password: String, callback: (Boolean) -> Unit) {
        remote.login(email, password) {
            local.userId = it?.getString("userId")
            local.authToken = it?.getString("id")
            local.authDate = it?.getString("created")
            local.username = it?.getJSONObject("user")?.getString("username")
            callback(it != null)
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

    fun getPartProgress(partId: String, callback: (progress: Double?) -> Unit) {
        val userId = local.userId
        if (userId != null && local.partsProgress == null) {
            remote.getUserPartProgressJson(userId) {
                if (it != null) local.partsProgress = PartsProgress(it)
                callback(local.partsProgress?.getProgress(partId))
            }
        } else {
            callback(local.partsProgress?.getProgress(partId))
        }
    }
}