package com.ytrewqwert.yetanotherjnovelreader.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.UserData
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.Part
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.progress.Progress
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.Serie
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.Volume
import com.ytrewqwert.yetanotherjnovelreader.data.remote.retrofit.JNCApiFactory
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import retrofit2.HttpException
import kotlin.coroutines.resume

/**
 * Exposes methods for fetching data from the remote database.
 *
 * Methods with nullable return values return null if the request failed for some reason.
 */
class RemoteRepository private constructor(
    appContext: Context,
    private var authToken: String?
) {
    companion object {
        private const val TAG = "RemoteRepository"
        /** The address for J-Novel Club's backend API. */
        const val API_ADDR = "https://api.j-novel.club/api"
        /** The address where J-Novel Club stores images. */
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
    suspend fun getPartHtml(partId: String) =
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

    suspend fun getSeries(amount: Int, offset: Int, seriesFilters: List<String>? = null): List<Serie>? {
        val filters = UrlParameterBuilder().apply {
            addLimit(amount)
            addOffset(offset)
            if (seriesFilters != null) addWhereFieldInList("id", seriesFilters)
        }.toString()

        val rawSeries = JNCApiFactory.jncApi.getSeries(filters)
        Log.d(TAG, "SeriesRequest: Found ${rawSeries.size} series")
        return rawSeries.map { Serie.fromSerieRaw(it) }
    }
    suspend fun getSerieVolumes(serieId: String, amount: Int, offset: Int): List<Volume>? {
        val filters = UrlParameterBuilder().apply {
            addLimit(amount)
            addOffset(offset)
            addWhere("serieId", "\"$serieId\"")
        }.toString()

        val rawVolumes = JNCApiFactory.jncApi.getVolumes(filters)
        Log.d(TAG, "SerieVolumesRequest: Found ${rawVolumes.size} volumes")
        return rawVolumes.map { Volume.fromVolumeRaw(it) }
    }
    suspend fun getVolumeParts(volumeId: String, amount: Int, offset: Int): List<Part>? {
        val filters = UrlParameterBuilder().apply {
            addLimit(amount)
            addOffset(offset)
            addWhere("volumeId", "\"$volumeId\"")
        }.toString()

        val rawParts = JNCApiFactory.jncApi.getParts(filters)
        Log.d(TAG, "VolumePartsRequest: Found ${rawParts.size} parts")
        return rawParts.map { Part.fromPartRaw(it) }
    }
    suspend fun getRecentParts(amount: Int, offset: Int, seriesFilters: List<String>? = null): List<Part>? {
        val filters = UrlParameterBuilder().apply {
            addOrder("launchDate+DESC")
            addLimit(amount)
            addOffset(offset)
            if (seriesFilters != null) addWhereFieldInList("serieId", seriesFilters)
        }.toString()

        val rawParts = JNCApiFactory.jncApi.getParts(filters)
        Log.d(TAG, "RecentPartsRequest: Found ${rawParts.size} parts")
        return rawParts.map { Part.fromPartRaw(it) }
    }
    suspend fun getUpNextParts(parts: List<Pair<String, Int>>): List<Part> {
        val resultParts = ArrayList<Part>()
        coroutineScope {
            val jobs = parts.map {
                async {
                    val filters = UrlParameterBuilder().apply {
                        addWhere("serieId", "\"${it.first}\"")
                        addWhere("partNumber", "${it.second}")
                    }.toString()

                    val rawPart = JNCApiFactory.jncApi.getPart(filters)
                    Part.fromPartRaw(rawPart)
                }
            }

            for (job in jobs) {
                try {
                    val part = job.await()
                    resultParts.add(part)
                } catch (e: HttpException) {
                    if (e.code() != 404) throw e // 404 returned if the part doesn't exist...
                }
            }
        }
        Log.d(TAG, "UpNextPartsRequest: Found ${resultParts.size}/${parts.size} parts")
        return resultParts
    }

    suspend fun getUserPartProgress(userId: String) =
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
    /** Returns true if successful, false otherwise. */
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