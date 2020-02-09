package com.example.yetanotherjnovelreader.data

import android.content.Context

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

    private val local = LocalRepository.getInstance()
    private val remote = RemoteRepository.getInstance(appContext)

    fun login(email: String, password: String, callback: (Boolean) -> Unit) {
        remote.login(email, password, callback)
    }

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
}