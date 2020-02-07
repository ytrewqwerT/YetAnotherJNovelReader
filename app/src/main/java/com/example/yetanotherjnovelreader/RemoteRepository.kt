package com.example.yetanotherjnovelreader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.jakewharton.disklrucache.DiskLruCache
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class RemoteRepository private constructor(appContext: Context) {
    companion object {
        private const val TAG = "RemoteRepository"
        const val API_ADDR = "https://api.j-novel.club/api"
        const val IMG_ADDR = "https://d2dq7ifhe7bu0f.cloudfront.net"

        @Volatile
        private var INSTANCE: RemoteRepository? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: RemoteRepository(context.applicationContext).also {
                    INSTANCE = it
                }
            }
    }

    private val requestQueue: RequestQueue by lazy { Volley.newRequestQueue(appContext) }
    val imageLoader = ImageLoader(requestQueue, DiskImageLoader(appContext))

    fun login(email: String, password: String) {
        val args = JSONObject().put("email", email).put("password", password)

        val request = JsonObjectRequest(
            Request.Method.POST,
            "${API_ADDR}/users/login?include=user",
            args,
            Response.Listener { Log.d(TAG, "LoginSuccess: ${it.toString(4)}") },
            Response.ErrorListener { Log.d(TAG, "LoginFailure: $it") }
        )

        requestQueue.add(request)
    }

    fun getSeries(listener: (seriesList: List<Series>) -> Unit) {
        val request = JsonArrayRequest(
            Request.Method.GET,
            "${API_ADDR}/series",
            null,
            Response.Listener<JSONArray> {
                Log.d(TAG, "SeriesSuccess: Found ${it.length()} series")
                Log.d(TAG, it.toString(4))

                val resultList = ArrayList<Series>(it.length())
                for (i in 0 until it.length()) resultList.add(Series(it.getJSONObject(i)))
                listener(resultList)
            },
            Response.ErrorListener { Log.d(TAG, "SeriesFailure: $it") }
        )
        requestQueue.add(request)
    }

    private class DiskImageLoader(appContext: Context) : ImageLoader.ImageCache {
        companion object {
            private const val MAX_KEY_LEN = 64
        }
        private val cache: DiskLruCache
        init {
            val cacheDir = File(appContext.cacheDir, "disklrucache")
            cache = DiskLruCache.open(cacheDir, BuildConfig.VERSION_CODE, 1, 1 shl 24)
        }
        override fun getBitmap(url: String?): Bitmap? {
            if (url == null) return null
            val snapshot = cache.get(urlToName(url)) ?: return null
            val bytes = snapshot.getInputStream(0).readBytes()
            val result = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            Log.d(TAG, "Got $result from $url")
            return result
        }
        override fun putBitmap(url: String?, bitmap: Bitmap?) {
            if (url == null || bitmap == null) return
            cache.edit(urlToName(url))?.apply {
                val stream = newOutputStream(0)
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                    Log.i(TAG, "Encoded $bitmap for $url")
                    commit()
                } else {
                    Log.e(TAG, "Failed to encode $bitmap for $url")
                    abort()
                }
            }
        }

        private fun urlToName(url: String): String {
            var result = if (url.length <= MAX_KEY_LEN) {
                url.toLowerCase(Locale.ENGLISH)
            } else {
                url.substring(url.length - MAX_KEY_LEN).toLowerCase(Locale.ENGLISH)
            }
            result = result.replace(Regex("[^a-z^0-9]"), "_")
            return result
        }
    }
}
