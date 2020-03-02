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
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import kotlin.coroutines.resume

class RemoteRepository private constructor(
    appContext: Context,
    private var authToken: String?
) {

    companion object {
        private const val TAG = "RemoteRepository"
        const val API_ADDR = "https://api.j-novel.club/api"
        const val IMG_ADDR = "https://d2dq7ifhe7bu0f.cloudfront.net"

        @Volatile
        private var INSTANCE: RemoteRepository? = null
        fun getInstance(context: Context, authToken: String? = null) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: RemoteRepository(context.applicationContext, authToken).also {
                    INSTANCE = it
                }
            }
    }

    private val requestQueue: RequestQueue by lazy { Volley.newRequestQueue(appContext) }
    private val imageLoader = ImageLoader(requestQueue, DiskImageLoader(appContext))

    fun getImage(source: String, callback: (Bitmap?) -> Unit) {
        imageLoader.get(source, object : ImageLoader.ImageListener {
            override fun onResponse(response: ImageLoader.ImageContainer?, isImmediate: Boolean) {
                Log.d(TAG, "ImageSuccess: Source = $source")
                callback(response?.bitmap)
            }
            override fun onErrorResponse(error: VolleyError?) {
                Log.w(TAG, "ImageFailure: $error")
                callback(null)
            }
        })
    }

    fun getPartContentJson(partId: String, callback: (partJson: JSONObject?) -> Unit) {
        val url = "$API_ADDR/parts/${partId}/partData"
        val request = AuthorizedJsonObjectRequest(
            authToken, Request.Method.GET, url, null,
            Response.Listener {
                Log.d(TAG, "PartSuccess: Found part $partId")
                Log.v(TAG, it.toString(4))
                callback(it)
            },
            Response.ErrorListener {
                Log.w(TAG, "PartFailure: $it")
                callback(null)
            }
        )
        requestQueue.add(request)
    }
    fun getPartsJsonAfter(time: Instant, callback: (partsJson: JSONArray) -> Unit) {
        val url = ParameterizedURLBuilder("$API_ADDR/parts")
            .addFilter("launchDate", "{\"gt\":\"${time}\"}")
            .addOrder("launchDate+DESC")
            .build()
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

    fun getSeriesJson(callback: (seriesJson: JSONArray) -> Unit) {
        val url = "$API_ADDR/series"
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener<JSONArray> {
                Log.d(TAG, "SeriesSuccess: Found ${it.length()} series")
                Log.v(TAG, it.toString(4))
                callback(it)
            },
            Response.ErrorListener { Log.w(TAG, "SeriesFailure: $it") }
        )
        requestQueue.add(request)
    }
    fun getSerieVolumesJson(serieId: String, callback: (volumesJson: JSONArray) -> Unit) {
        val url = ParameterizedURLBuilder("$API_ADDR/volumes")
            .addFilter("serieId", serieId)
            .build()
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener {
                Log.d(TAG, "SeriesVolumesSuccess: Found ${it.length()} volumes")
                callback(it)
            },
            Response.ErrorListener { Log.w(TAG, "SeriesVolumesFailure: $it") }
        )
        requestQueue.add(request)
    }
    fun getVolumePartsJson(volumeId: String, callback: (partsJson: JSONArray) -> Unit) {
        val url = ParameterizedURLBuilder("$API_ADDR/parts")
            .addFilter("volumeId", volumeId)
            .build()
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener {
                Log.d(TAG, "VolumePartsSuccess: Found ${it.length()} parts")
                callback(it)
            },
            Response.ErrorListener { Log.w(TAG, "VolumePartsFailure: $it") }
        )
        requestQueue.add(request)
    }

    suspend fun getUserPartProgressJson(userId: String) =
        suspendCancellableCoroutine<JSONArray?> { cont ->
            val url = ParameterizedURLBuilder("$API_ADDR/users/$userId")
                .addInclude("readParts")
                .build()
            val request = AuthorizedJsonObjectRequest(
                authToken, Request.Method.GET, url, null,
                Response.Listener {
                    val partProgress = it.getJSONArray("readParts")
                    Log.d(TAG, "PartProgressSuccess: Found ${partProgress.length()} parts")
                    Log.v(TAG, partProgress.toString(4))
                    cont.resume(partProgress)
                },
                Response.ErrorListener {
                    Log.w(TAG, "PartProgressFailure: $it")
                    cont.resume(null)
                }
            )
            requestQueue.add(request)
        }

    suspend fun login(email: String, password: String): JSONObject? {
        return suspendCancellableCoroutine { cont ->
            val args = JSONObject().put("email", email).put("password", password)

            val request = JsonObjectRequest(
                Request.Method.POST,
                "$API_ADDR/users/login?include=user",
                args,
                Response.Listener {
                    Log.d(TAG, "LoginSuccess: ${it.toString(4)}")
                    authToken = it?.getString("id")
                    cont.resume(it)
                },
                Response.ErrorListener {
                    Log.w(TAG, "LoginFailure: $it")
                    cont.resume(null)
                }
            )
            requestQueue.add(request)
        }
    }
    suspend fun logout() = suspendCancellableCoroutine<Boolean> { cont ->
        val request = AuthorizedStringRequest(
            authToken, Request.Method.POST,
            "$API_ADDR/users/logout",
            Response.Listener {
                Log.d(TAG, "LogoutSuccess")
                authToken = null
                cont.resume(true)
            },
            Response.ErrorListener {
                Log.w(TAG, "LogoutFailure? $it")
                cont.resume(false)
            }
        )
        requestQueue.add(request)
    }

    fun setUserPartProgress(userId: String, partId: String, progress: Double) {
        val args = JSONObject().put("partId", partId).put("completion", progress)
        val request = AuthorizedJsonObjectRequest(
            authToken, Request.Method.POST,
            "$API_ADDR/users/${userId}/updateReadCompletion",
            args,
            Response.Listener { Log.d(TAG, "SaveProgressSuccess: $partId at $progress") },
            Response.ErrorListener { Log.w(TAG, "SaveProgressFailure: $it") }
        )
        requestQueue.add(request)
    }
}