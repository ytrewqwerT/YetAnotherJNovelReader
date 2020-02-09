package com.example.yetanotherjnovelreader.data

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class RemoteRepository private constructor(appContext: Context) {
    companion object {
        private const val TAG = "RemoteRepository"
        const val API_ADDR = "https://api.j-novel.club/api"
        const val IMG_ADDR = "https://d2dq7ifhe7bu0f.cloudfront.net"

        @Volatile
        private var INSTANCE: RemoteRepository? = null
        fun getInstance(context: Context) =
            INSTANCE
                ?: synchronized(this) {
                INSTANCE
                    ?: RemoteRepository(
                        context.applicationContext
                    ).also {
                    INSTANCE = it
                }
            }
    }

    private val requestQueue: RequestQueue by lazy { Volley.newRequestQueue(appContext) }
    val imageLoader = ImageLoader(requestQueue,
        DiskImageLoader(appContext)
    )

    fun login(email: String, password: String, callback: (Boolean) -> Unit) {
        val args = JSONObject().put("email", email).put("password", password)

        val request = JsonObjectRequest(
            Request.Method.POST,
            "$API_ADDR/users/login?include=user",
            args,
            Response.Listener {
                Log.d(TAG, "LoginSuccess: ${it.toString(4)}")
                callback(true)
            },
            Response.ErrorListener {
                Log.d(TAG, "LoginFailure: $it")
                callback(false)
            }
        )

        requestQueue.add(request)
    }

    fun getSeriesJson(callback: (seriesJson: JSONArray) -> Unit) {
        val request = JsonArrayRequest(
            Request.Method.GET,
            "$API_ADDR/series",
            null,
            Response.Listener<JSONArray> {
                Log.d(TAG, "SeriesSuccess: Found ${it.length()} series")
                Log.d(TAG, it.toString(4))
                callback(it)
            },
            Response.ErrorListener { Log.d(TAG, "SeriesFailure: $it") }
        )
        requestQueue.add(request)
    }
}
