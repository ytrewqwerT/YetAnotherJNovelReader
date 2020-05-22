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
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.UserData
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.Part
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.progress.Progress
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.Serie
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.Volume
import kotlinx.coroutines.suspendCancellableCoroutine
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

    suspend fun getImage(source: String) =
        suspendCancellableCoroutine<Bitmap?> { cont ->
        imageLoader.get(source, object : ImageLoader.ImageListener {
            override fun onResponse(response: ImageLoader.ImageContainer?, isImmediate: Boolean) {
                Log.d(TAG, "ImageSuccess: Source = $source")
                val bitmap = response?.bitmap
                if (bitmap != null) {
                    cont.resume(response.bitmap)
                } else if (!isImmediate) {
                    throw IllegalStateException("Volley successfully retrieved a null image?")
                }
            }
            override fun onErrorResponse(error: VolleyError?) {
                Log.w(TAG, "ImageFailure: $error")
                cont.resume(null)
            }
        })
    }
    suspend fun getPartContentJson(partId: String) =
        suspendCancellableCoroutine<String?> { cont ->
            val url = "$API_ADDR/parts/${partId}/partData"
            val request = AuthorizedJsonObjectRequest(
                authToken, Request.Method.GET, url, null,
                Response.Listener {
                    Log.d(TAG, "PartSuccess: Found part $partId")
                    Log.v(TAG, it.toString(4))
                    cont.resume(it.getString("dataHTML"))
                },
                Response.ErrorListener {
                    Log.w(TAG, "PartFailure: $it")
                    cont.resume(null)
                }
            )
            requestQueue.add(request)
        }

    suspend fun getSeriesJson(amount: Int, offset: Int) =
        suspendCancellableCoroutine<List<Serie>?> { cont ->
            val url = ParameterizedURLBuilder("$API_ADDR/series")
                .addBaseFilter("limit", "$amount")
                .addBaseFilter("offset", "$offset")
                .build()
            val request = JsonArrayRequest(
                Request.Method.GET, url, null,
                Response.Listener {
                    Log.d(TAG, "SeriesSuccess: Found ${it.length()} series")
                    Log.v(TAG, it.toString(4))
                    cont.resume(Serie.fromJson(it))
                },
                Response.ErrorListener {
                    Log.w(TAG, "SeriesFailure: $it")
                    cont.resume(null)
                }
            )
            requestQueue.add(request)
        }
    suspend fun getSerieVolumesJson(serieId: String, amount: Int, offset: Int) =
        suspendCancellableCoroutine<List<Volume>?> { cont ->
            val url = ParameterizedURLBuilder("$API_ADDR/volumes")
                .addFilter("serieId", serieId)
                .addBaseFilter("limit", "$amount")
                .addBaseFilter("offset", "$offset")
                .build()
            val request = JsonArrayRequest(
                Request.Method.GET, url, null,
                Response.Listener {
                    Log.d(TAG, "SeriesVolumesSuccess: Found ${it.length()} volumes")
                    cont.resume(Volume.fromJson(it))
                },
                Response.ErrorListener {
                    Log.w(TAG, "SeriesVolumesFailure: $it")
                    cont.resume(null)
                }
            )
            requestQueue.add(request)
        }
    suspend fun getVolumePartsJson(volumeId: String, amount: Int, offset: Int) =
        suspendCancellableCoroutine<List<Part>?> { cont ->
            val url = ParameterizedURLBuilder("$API_ADDR/parts")
                .addFilter("volumeId", volumeId)
                .addBaseFilter("limit", "$amount")
                .addBaseFilter("offset", "$offset")
                .build()
            val request = JsonArrayRequest(
                Request.Method.GET, url, null,
                Response.Listener {
                    Log.d(TAG, "VolumePartsSuccess: Found ${it.length()} parts")
                    cont.resume(Part.fromJson(it))
                },
                Response.ErrorListener {
                    Log.w(TAG, "VolumePartsFailure: $it")
                    cont.resume(null)
                }
            )
            requestQueue.add(request)
        }
    suspend fun getPartsJsonAfter(time: Instant, amount: Int, offset: Int) =
        suspendCancellableCoroutine<List<Part>?> { cont ->
            val url = ParameterizedURLBuilder("$API_ADDR/parts")
                .addFilter("launchDate", "{\"gt\":\"${time}\"}")
                .addBaseFilter("order", "launchDate+DESC")
                .addBaseFilter("limit", "$amount")
                .addBaseFilter("offset", "$offset")
                .build()
            val request = JsonArrayRequest(
                Request.Method.GET, url, null,
                Response.Listener {
                    Log.d(TAG, "RecentPartSuccess: Found ${it.length()} parts")
                    cont.resume(Part.fromJson(it))
                },
                Response.ErrorListener {
                    Log.w(TAG, "RecentPartFailure: $it")
                    cont.resume(null)
                }
            )
            requestQueue.add(request)
        }

    suspend fun getUserPartProgressJson(userId: String) =
        suspendCancellableCoroutine<List<Progress>?> { cont ->
            val url = ParameterizedURLBuilder("$API_ADDR/users/$userId")
                .addInclude("readParts")
                .build()
            val request = AuthorizedJsonObjectRequest(
                authToken, Request.Method.GET, url, null,
                Response.Listener {
                    val partProgress = it.getJSONArray("readParts")
                    Log.d(TAG, "PartProgressSuccess: Found ${partProgress.length()} parts")
                    Log.v(TAG, partProgress.toString(4))
                    cont.resume(Progress.fromJson(partProgress))
                },
                Response.ErrorListener {
                    Log.w(TAG, "PartProgressFailure: $it")
                    cont.resume(null)
                }
            )
            requestQueue.add(request)
        }
    suspend fun setUserPartProgress(userId: String, partId: String, progress: Double) =
        suspendCancellableCoroutine<Boolean> { cont ->
            val args = JSONObject().put("partId", partId).put("completion", progress)
            val request = AuthorizedJsonObjectRequest(
                authToken, Request.Method.POST,
                "$API_ADDR/users/${userId}/updateReadCompletion",
                args,
                Response.Listener {
                    Log.d(TAG, "SaveProgressSuccess: $partId at $progress")
                    cont.resume(true)
                },
                Response.ErrorListener {
                    Log.w(TAG, "SaveProgressFailure: $it")
                    cont.resume(false)
                }
            )
            requestQueue.add(request)
        }

    suspend fun login(email: String, password: String) =
        suspendCancellableCoroutine<UserData?> { cont ->
            val args = JSONObject().put("email", email).put("password", password)

            val request = JsonObjectRequest(
                Request.Method.POST,
                "$API_ADDR/users/login?include=user",
                args,
                Response.Listener {
                    Log.d(TAG, "LoginSuccess: ${it.toString(4)}")
                    authToken = it?.getString("id")
                    cont.resume(UserData.fromJson(it))
                },
                Response.ErrorListener {
                    Log.w(TAG, "LoginFailure: $it")
                    cont.resume(null)
                }
            )
            requestQueue.add(request)
        }
    suspend fun logout() =
        suspendCancellableCoroutine<Boolean> { cont ->
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

}