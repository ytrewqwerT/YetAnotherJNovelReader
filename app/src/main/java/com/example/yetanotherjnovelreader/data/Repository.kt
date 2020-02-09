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
        if (local.series.isEmpty()) {
            remote.getSeriesJson {
                local.addSeriesInfo(it)
                callback(local.series)
            }
        } else {
            callback(local.series)
        }
    }
}