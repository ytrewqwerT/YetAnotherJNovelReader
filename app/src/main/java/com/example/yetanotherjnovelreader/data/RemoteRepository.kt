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

private const val TAG = "RemoteRepository"

class RemoteRepository private constructor(
    appContext: Context,
    private var authToken: String?
) {
    companion object {
        const val API_ADDR = "https://api.j-novel.club/api"
        const val IMG_ADDR = "https://d2dq7ifhe7bu0f.cloudfront.net"

        @Volatile
        private var INSTANCE: RemoteRepository? = null
        fun getInstance(context: Context, authToken: String? = null) =
            INSTANCE
                ?: synchronized(this) {
                INSTANCE
                    ?: RemoteRepository(
                        context.applicationContext, authToken
                    ).also {
                    INSTANCE = it
                }
            }
    }

    private val requestQueue: RequestQueue by lazy { Volley.newRequestQueue(appContext) }
    val imageLoader = ImageLoader(requestQueue,
        DiskImageLoader(appContext)
    )

    fun login(email: String, password: String, callback: (String?) -> Unit) {
        val args = JSONObject().put("email", email).put("password", password)

        val request = JsonObjectRequest(
            Request.Method.POST,
            "$API_ADDR/users/login?include=user",
            args,
            Response.Listener {
                Log.d(TAG, "LoginSuccess: ${it.toString(4)}")
                authToken = it.getString("id")
                callback(authToken)
            },
            Response.ErrorListener {
                Log.d(TAG, "LoginFailure: $it")
                callback(null)
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

    fun getSerieJson(serieId: String, callback: (serieJson: JSONObject) -> Unit) {
        val url = "${API_ADDR}/series/findOne?filter=" +
                "{\"where\":{\"id\":\"${serieId}\"},\"include\":[\"volumes\",\"parts\"]}"
        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener<JSONObject> {
                Log.d(TAG, "SerieSuccess: Found series $serieId")
                Log.d(TAG, it.toString(4))
                callback(it)
            },
            Response.ErrorListener { Log.d(TAG, "SerieFailure: $it") }
        )
        requestQueue.add(request)
    }

    fun getPartJson(partId: String, callback: (partJson: JSONObject?) -> Unit) {
        val request = AuthorizedJsonObjectRequest(
            authToken,
            Request.Method.GET,
            "${API_ADDR}/parts/${partId}/partData",
            null,
            Response.Listener {
                Log.d(TAG, "PartSuccess: Found part $partId")
                Log.d(TAG, it.toString(4))
                callback(it)
            },
            Response.ErrorListener { Log.d(TAG, "PartFailure: $it") }
        )
        requestQueue.add(request)
    }
}