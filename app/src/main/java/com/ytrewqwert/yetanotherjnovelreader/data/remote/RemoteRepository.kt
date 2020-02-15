package com.ytrewqwert.yetanotherjnovelreader.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant

class RemoteRepository private constructor(
    appContext: Context,
    var authToken: String?
) {

    companion object {
        private const val TAG = "RemoteRepository"
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
        DiskImageLoader(
            appContext
        )
    )

    fun login(email: String, password: String, callback: (JSONObject?) -> Unit) {
        val args = JSONObject().put("email", email).put("password", password)

        val request = JsonObjectRequest(
            Request.Method.POST,
            "$API_ADDR/users/login?include=user",
            args,
            Response.Listener {
                Log.d(TAG, "LoginSuccess: ${it.toString(4)}")
                authToken = it?.getString("id")
                callback(it)
            },
            Response.ErrorListener {
                Log.w(TAG, "LoginFailure: $it")
                callback(null)
            }
        )
        requestQueue.add(request)
    }

    fun logout(callback: (Boolean) -> Unit) {
        val request =
            AuthorizedStringRequest(
                authToken,
                Request.Method.POST,
                "$API_ADDR/users/logout",
                Response.Listener {
                    Log.d(
                        TAG,
                        "LogoutSuccess"
                    )
                    authToken = null
                    callback(true)
                },
                Response.ErrorListener {
                    Log.w(
                        TAG,
                        "LogoutFailure? $it"
                    )
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
                Log.v(TAG, it.toString(4))
                callback(it)
            },
            Response.ErrorListener { Log.w(TAG, "SeriesFailure: $it") }
        )
        requestQueue.add(request)
    }

    fun getSerieJson(serieId: String, callback: (serieJson: JSONObject) -> Unit) {
        val url = "$API_ADDR/series/findOne?filter=" +
                "{\"where\":{\"id\":\"${serieId}\"},\"include\":[\"volumes\",\"parts\"]}"
        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener<JSONObject> {
                Log.d(TAG, "SerieSuccess: Found series $serieId")
                Log.v(TAG, it.toString(4))
                callback(it)
            },
            Response.ErrorListener { Log.w(TAG, "SerieFailure: $it") }
        )
        requestQueue.add(request)
    }

    fun getPartsJsonAfter(time: Instant, callback: (partsJson: JSONArray) -> Unit) {
        val url = "$API_ADDR/parts?filter=" +
                "{\"where\":{\"launchDate\":{\"gt\":\"${time}\"}},\"order\":\"launchDate+DESC\"}"
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener {
                Log.d(TAG, "RecentPartSuccess: Found ${it.length()} parts")
                callback(it)
            },
            Response.ErrorListener { Log.w(TAG, "RecentPartFailure: $it") }
        )
        requestQueue.add(request)
    }

    fun getPartJson(partId: String, callback: (partJson: JSONObject?) -> Unit) {
        val request =
            AuthorizedJsonObjectRequest(
                authToken,
                Request.Method.GET,
                "$API_ADDR/parts/${partId}/partData",
                null,
                Response.Listener {
                    Log.d(
                        TAG,
                        "PartSuccess: Found part $partId"
                    )
                    Log.v(
                        TAG,
                        it.toString(4)
                    )
                    callback(it)
                },
                Response.ErrorListener {
                    Log.w(
                        TAG,
                        "PartFailure: $it"
                    )
                }
            )
        requestQueue.add(request)
    }

    fun getUserPartProgressJson(userId: String, callback: (partProgress: JSONArray?) -> Unit) {
        val url = "$API_ADDR/users/${userId}?filter={\"include\":\"readParts\"}"
        val request =
            AuthorizedJsonObjectRequest(
                authToken, Request.Method.GET, url, null,
                Response.Listener {
                    val partProgress = it.getJSONArray("readParts")
                    Log.d(
                        TAG,
                        "PartProgressSuccess: Found progress ${partProgress.length()} parts"
                    )
                    Log.v(
                        TAG,
                        partProgress.toString(4)
                    )
                    callback(partProgress)
                },
                Response.ErrorListener {
                    Log.w(
                        TAG,
                        "PartProgressFailure: $it"
                    )
                }
            )
        requestQueue.add(request)
    }
    fun setUserPartProgress(userId: String, partId: String, progress: Double) {
        val args = JSONObject().put("partId", partId).put("completion", progress)
        val request =
            AuthorizedJsonObjectRequest(
                authToken, Request.Method.POST,
                "$API_ADDR/users/${userId}/updateReadCompletion",
                args,
                Response.Listener {
                    Log.d(
                        TAG,
                        "SaveProgressSuccess: $partId at $progress"
                    )
                },
                Response.ErrorListener {
                    Log.w(
                        TAG,
                        "SaveProgressFailure: $it"
                    )
                }
            )
        requestQueue.add(request)
    }

    fun getImage(source: String, callback: (Bitmap?) -> Unit) {
        imageLoader.get(source, object : ImageLoader.ImageListener {
            override fun onResponse(response: ImageLoader.ImageContainer?, isImmediate: Boolean) {
                Log.d(TAG, "Got response for image ${source}: ${response?.bitmap}")
                callback(response?.bitmap)
            }
            override fun onErrorResponse(error: VolleyError?) {
                Log.w(TAG, "Failed to get image: $error")
                callback(null)
            }
        })
    }
}