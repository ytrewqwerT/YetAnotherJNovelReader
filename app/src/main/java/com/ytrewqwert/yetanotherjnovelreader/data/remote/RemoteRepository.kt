package com.ytrewqwert.yetanotherjnovelreader.data.remote

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import coil.Coil
import coil.request.GetRequest
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.UserData
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.Part
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.progress.Progress
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.Serie
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.Volume
import com.ytrewqwert.yetanotherjnovelreader.data.remote.retrofit.JNCApiFactory
import com.ytrewqwert.yetanotherjnovelreader.data.remote.retrofit.model.LoginRaw
import com.ytrewqwert.yetanotherjnovelreader.data.remote.retrofit.model.ProgressRaw
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope

/**
 * Exposes methods for fetching data from the remote database.
 *
 * Methods with nullable return values return null if the request failed for some reason.
 */
class RemoteRepository private constructor(
    private val appContext: Context,
    private var authToken: String?
) {
    companion object {
        private const val TAG = "RemoteRepository"
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

    private val imageLoader = Coil.imageLoader(appContext)

    suspend fun getImage(source: String): Drawable? {
        val request = GetRequest.Builder(appContext)
            .data(source)
            .build()
        val result = imageLoader.execute(request)
        return result.drawable
    }

    suspend fun getPartHtml(partId: String): String? {
        val rawPartContent = safeNetworkCall("PartFailure") {
            JNCApiFactory.jncApi.getPartHtml(authToken, partId)
        } ?: return null
        Log.d(TAG, "PartSuccess: Found part $partId")
        return rawPartContent.dataHTML
    }

    suspend fun getSeries(amount: Int, offset: Int, seriesFilters: List<String>? = null): List<Serie>? {
        val filters = UrlParameterBuilder().apply {
            addLimit(amount)
            addOffset(offset)
            if (seriesFilters != null) addWhereFieldInList("id", seriesFilters)
        }.toString()

        val rawSeries = safeNetworkCall("SeriesFailure") {
            JNCApiFactory.jncApi.getSeries(filters)
        } ?: return null
        Log.d(TAG, "SeriesSuccess: Found ${rawSeries.size} series")
        return rawSeries.map { Serie.fromSerieRaw(it) }
    }

    suspend fun getSerieVolumes(serieId: String, amount: Int, offset: Int): List<Volume>? {
        val filters = UrlParameterBuilder().apply {
            addLimit(amount)
            addOffset(offset)
            addWhere("serieId", "\"$serieId\"")
        }.toString()

        val rawVolumes = safeNetworkCall("SerieVolumesFailure") {
            JNCApiFactory.jncApi.getVolumes(filters)
        } ?: return null
        Log.d(TAG, "SerieVolumesSuccess: Found ${rawVolumes.size} volumes")
        return rawVolumes.map { Volume.fromVolumeRaw(it) }
    }

    suspend fun getVolumeParts(volumeId: String, amount: Int, offset: Int): List<Part>? {
        val filters = UrlParameterBuilder().apply {
            addLimit(amount)
            addOffset(offset)
            addWhere("volumeId", "\"$volumeId\"")
        }.toString()

        val rawParts = safeNetworkCall("VolumePartsFailure") {
            JNCApiFactory.jncApi.getParts(filters)
        } ?: return null
        Log.d(TAG, "VolumePartsSuccess: Found ${rawParts.size} parts")
        return rawParts.map { Part.fromPartRaw(it) }
    }

    suspend fun getRecentParts(amount: Int, offset: Int, seriesFilters: List<String>? = null): List<Part>? {
        val filters = UrlParameterBuilder().apply {
            addOrder("launchDate+DESC")
            addLimit(amount)
            addOffset(offset)
            if (seriesFilters != null) addWhereFieldInList("serieId", seriesFilters)
        }.toString()

        val rawParts = safeNetworkCall("RecentPartsFailure") {
            JNCApiFactory.jncApi.getParts(filters)
        } ?: return null
        Log.d(TAG, "RecentPartsSuccess: Found ${rawParts.size} parts")
        return rawParts.map { Part.fromPartRaw(it) }
    }

    suspend fun getUpNextParts(parts: List<Pair<String, Int>>): List<Part> {
        val resultParts = ArrayList<Part>()
        supervisorScope {
            val jobs = parts.map {
                async {
                    val filters = UrlParameterBuilder().apply {
                        addWhere("serieId", "\"${it.first}\"")
                        addWhere("partNumber", "${it.second}")
                    }.toString()

                    val rawPart = safeNetworkCall("UpNextPartFailure") {
                        JNCApiFactory.jncApi.getPart(filters)
                    } ?: return@async null
                    Part.fromPartRaw(rawPart)
                }
            }

            for (job in jobs) {
                val part = job.await()
                resultParts.add(part ?: continue)
            }
        }
        Log.d(TAG, "UpNextParts: Found ${resultParts.size}/${parts.size} parts")
        return resultParts
    }

    suspend fun getUserPartProgress(userId: String): List<Progress>? {
        val filters = UrlParameterBuilder().apply {
            addInclude("readParts")
        }.toString()

        val rawUserWithProgress = safeNetworkCall("LoadProgressFailure") {
            JNCApiFactory.jncApi.getUser(authToken, userId, filters)
        } ?: return null
        val rawProgress = rawUserWithProgress.readParts ?: return null
        Log.d(TAG, "LoadProgressSuccess: Found ${rawProgress.size} parts")
        return rawProgress.map { Progress.fromProgressRaw(it) }
    }

    /** Returns true if successful, false otherwise. */
    suspend fun setUserPartProgress(userId: String, partId: String, progress: Double): Boolean {
        val progressRaw = ProgressRaw(partId, progress.toFloat())
        safeNetworkCall("SaveProgressFailure") {
            JNCApiFactory.jncApi.setProgress(authToken, userId, progressRaw)
        } ?: return false
        Log.d(TAG, "SaveProgressSuccess: $partId at ${progress.toFloat()}")
        return true
    }

    suspend fun login(email: String, password: String): UserData? {
        val credentials = LoginRaw(email, password)
        val rawUser = safeNetworkCall("LoginFailure") {
            JNCApiFactory.jncApi.login(credentials)
        } ?: return null
        Log.d(TAG, "LoginSuccess")
        authToken = rawUser.authToken
        return UserData.fromUserRaw(rawUser)
    }

    suspend fun logout(): Boolean {
        safeNetworkCall("LogoutFailure") { JNCApiFactory.jncApi.logout(authToken) }
        Log.d(TAG, "LogoutSuccess")
        authToken = null
        return true
    }

    private suspend fun <T> safeNetworkCall(failMsg: String, request: suspend () -> T): T? {
        return try {
            request()
        } catch (tr: Throwable) {
            Log.w(TAG, "NetworkCallFailure ($failMsg): $tr")
            null
        }
    }
}