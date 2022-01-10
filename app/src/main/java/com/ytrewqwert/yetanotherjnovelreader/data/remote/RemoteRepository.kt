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
import com.ytrewqwert.yetanotherjnovelreader.data.remote.model.LoginRaw
import com.ytrewqwert.yetanotherjnovelreader.data.remote.model.OutboundFollowRaw
import com.ytrewqwert.yetanotherjnovelreader.data.remote.model.ProgressRaw
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
        /**
         * Retrieves or creates an instance of [RemoteRepository] tied to the application's context.
         *
         * @param[context] A Context object related to the application.
         * @param[authToken] The user's authorisation token, or null for no authorisation.
         */
        fun getInstance(context: Context, authToken: String? = null) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: RemoteRepository(context.applicationContext, authToken).also {
                    INSTANCE = it
                }
            }
    }

    private val jncApi = JNCApiFactory.jncApi
    private val imageLoader = Coil.imageLoader(appContext)

    /** Fetches the image referenced by the [source] url. */
    suspend fun getImage(source: String): Drawable? {
        val request = GetRequest.Builder(appContext)
            .data(source)
            .build()
        val result = imageLoader.execute(request)
        return result.drawable
    }

    /** Retrieves the contents of the part with id [partId] as a string formatted with html. */
    suspend fun getPartHtml(partId: String): String? {
        val rawPartContent = safeNetworkCall("PartFailure") {
            JNCApiRaw.getPartHtml(authToken, partId)
        } ?: return null
        Log.d(TAG, "PartSuccess: Found part $partId")
        return rawPartContent
    }

    /**
     * Fetches a list of all* available series.
     *
     * @param[amount] The maximum number of series to fetch.
     * @param[offset] An offset for how many series in the list to skip (for pagination).
     * @param[seriesFilters] A filter for which series to fetch; null for no filter.
     * @return A list of series with size no greater than [amount].
     */
    suspend fun getSeries(amount: Int, offset: Int, seriesFilters: List<String>? = null): List<Serie>? {
        val filters = UrlParameterBuilder().apply {
            addLimit(amount)
            addOffset(offset)
            if (seriesFilters != null) addWhereFieldInList("id", seriesFilters)
        }.toString()

        val rawSeries = safeNetworkCall("SeriesFailure") {
            jncApi.getSeries(filters)
        } ?: return null
        Log.d(TAG, "SeriesSuccess: Found ${rawSeries.size} series")
        return rawSeries.map { Serie.fromSerieRaw(it) }
    }

    /**
     * Fetches a list of the volumes in a series.
     *
     * @param[serieId] The id of the series to fetch volumes from.
     * @param[amount] The maximum number of volumes to fetch.
     * @param[offset] An offset for how many volumes in the list to skip (for pagination).
     * @return A list of volumes with size no greater than [amount].
     */
    suspend fun getSerieVolumes(serieId: String, amount: Int, offset: Int): List<Volume>? {
        val filters = UrlParameterBuilder().apply {
            addLimit(amount)
            addOffset(offset)
            addWhere("serieId", "\"$serieId\"")
        }.toString()

        val rawVolumes = safeNetworkCall("SerieVolumesFailure") {
            jncApi.getVolumes(filters)
        } ?: return null
        Log.d(TAG, "SerieVolumesSuccess: Found ${rawVolumes.size} volumes")
        return rawVolumes.map { Volume.fromVolumeRaw(it) }
    }

    /**
     * Fetches the volume with the given [volumeId].
     * @return The requested volume, or null if the fetch failed.
     */
    suspend fun getVolume(volumeId: String): Volume? {
        val filters = UrlParameterBuilder().apply {
            addWhere("id", "\"$volumeId\"")
        }.toString()

        val rawVolumes = safeNetworkCall("VolumeFailure") {
            jncApi.getVolumes(filters)
        } ?: return null
        Log.d(TAG, "VolumeSuccess: Found ${rawVolumes.size} volumes")
        val rawVolume = rawVolumes.firstOrNull() ?: return null
        return Volume.fromVolumeRaw(rawVolume)
    }

    /**
     * Fetches a list of the parts in a volume.
     *
     * @param[volumeId] The id of the volume to fetch parts from.
     * @param[amount] The maximum number of parts to fetch. No limit if 0.
     * @param[offset] An offset for how many parts in the list to skip (for pagination).
     * @return A list of parts with size no greater than [amount].
     */
    suspend fun getVolumeParts(volumeId: String, amount: Int, offset: Int): List<Part>? {
        val filters = UrlParameterBuilder().apply {
            if (amount > 0) addLimit(amount)
            addOffset(offset)
            addWhere("volumeId", "\"$volumeId\"")
        }.toString()

        val rawParts = safeNetworkCall("VolumePartsFailure") {
            jncApi.getParts(filters)
        } ?: return null
        Log.d(TAG, "VolumePartsSuccess: Found ${rawParts.size} parts")
        return rawParts.map { Part.fromPartRaw(it) }
    }

    /**
     * Fetches a list of all* available parts, most recently released first.
     *
     * @param[amount] The maximum number of parts to fetch.
     * @param[offset] An offset for how many parts in the list to skip (for pagination).
     * @param[seriesFilters] A filter for which series' parts to fetch; null for no filter.
     * @return A list of parts with size no greater than [amount].
     */
    suspend fun getRecentParts(amount: Int, offset: Int, seriesFilters: List<String>? = null): List<Part>? {
        val filters = UrlParameterBuilder().apply {
            addOrder("launchDate+DESC")
            addLimit(amount)
            addOffset(offset)
            if (seriesFilters != null) addWhereFieldInList("serieId", seriesFilters)
        }.toString()

        val rawParts = safeNetworkCall("RecentPartsFailure") {
            jncApi.getParts(filters)
        } ?: return null
        Log.d(TAG, "RecentPartsSuccess: Found ${rawParts.size} parts")
        return rawParts.map { Part.fromPartRaw(it) }
    }

    /**
     * Fetches a list of parts identified by a series id and part number in that series.
     *
     * @param[parts] A list of pairs, where each pair contains a series id and part number
     *  identifying a unique part
     * @return A list containing the requested parts that currently exist and are available.
     */
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
                        jncApi.getPart(filters)
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

    /** Fetches a list containing all of the user's (identified by [userId]) part progress data. */
    suspend fun getUserPartProgress(userId: String): List<Progress>? {
        val filters = UrlParameterBuilder().apply {
            addInclude("readParts")
        }.toString()

        val rawUserWithProgress = safeNetworkCall("LoadProgressFailure") {
            jncApi.getUser(authToken, userId, filters)
        } ?: return null
        val rawProgress = rawUserWithProgress.readParts ?: return null
        Log.d(TAG, "LoadProgressSuccess: Found ${rawProgress.size} parts")
        return rawProgress.map { Progress.fromProgressRaw(it) }
    }

    /**
     * Stores the user's progress in a part in the remote server.
     *
     * @param[userId] The id of the user who's progress is to be set.
     * @param[partId] The id of the part to be setting the progress of.
     * @param[progress] A number in the range [0,1] indicating the percentage progress to be set.
     * @return true if the progress was successfully set and false otherwise.
     */
    suspend fun setUserPartProgress(userId: String, partId: String, progress: Double): Boolean {
        val progressRaw = ProgressRaw(partId, progress.toFloat())
        safeNetworkCall("SaveProgressFailure") {
            jncApi.setProgress(authToken, userId, progressRaw)
        } ?: return false
        Log.d(TAG, "SaveProgressSuccess: $partId at ${progress.toFloat()}")
        return true
    }

    /** Fetches a list containing all of the user's (identified by [userId]) followed series. */
    suspend fun getUserSerieFollows(userId: String): List<String>? {
        val filters = UrlParameterBuilder().apply {
            addInclude("serieFollows")
        }.toString()

        val rawUserWithFollows = safeNetworkCall("LoadProgressFailure") {
            jncApi.getUser(authToken, userId, filters)
        } ?: return null
        val rawFollows = rawUserWithFollows.serieFollows ?: return null
        Log.d(TAG, "LoadFollowsSuccess: Found ${rawFollows.size} followed series")
        return rawFollows.map { it.serieId }
    }

    /** Sets the series with id [serieId] as followed by the user with id [userId]. */
    suspend fun followSerie(userId: String, serieId: String): Boolean {
        val followRaw = OutboundFollowRaw(serieId, 1) // (I think) 1 indicates novel.
        safeNetworkCall("FollowSerieFailure") {
            jncApi.followSerie(authToken, userId, followRaw)
        } ?: return false
        Log.d(TAG, "FollowSerieSuccess: Followed serie $serieId")
        return true
    }

    /** Sets the series with id [serieId] as not followed by the user with id [userId]. */
    suspend fun unfollowSerie(userId: String, serieId: String): Boolean {
        val followRaw = OutboundFollowRaw(serieId, 1) // (I think) 1 indicates novel.
        safeNetworkCall("UnfollowSerieFailure") {
            jncApi.unfollowSerie(authToken, userId, followRaw)
        } ?: return false
        Log.d(TAG, "UnfollowSerieSuccess: Unfollowed serie $serieId")
        return true
    }

    /**
     * Attempts to login using the given [email] and [password].
     *
     * @return The logged-in user's data if successful and null otherwise.
     */
    suspend fun login(email: String, password: String): UserData? {
        val credentials = LoginRaw(email, password)
        val rawUser = safeNetworkCall("LoginFailure") {
            jncApi.login(credentials)
        } ?: return null
        Log.d(TAG, "LoginSuccess")
        authToken = rawUser.authToken
        return UserData.fromUserRaw(rawUser)
    }

    /** Attempts to log the user out of the system. Returns true if successful, false otherwise. */
    suspend fun logout(): Boolean {
        safeNetworkCall("LogoutFailure") { jncApi.logout(authToken) }
        Log.d(TAG, "LogoutSuccess")
        authToken = null
        return true
    }

    /**
     * Executes the given function for a result, catching any exceptions that may occur.
     *
     * @param[failMsg] A failure message log if an exception occurs.
     * @param[request] The function to execute.
     * @return The result of the function call, or null if an exception occurred.
     */
    private suspend fun <T> safeNetworkCall(failMsg: String, request: suspend () -> T): T? {
        return try {
            request()
        } catch (tr: Throwable) {
            Log.w(TAG, "NetworkCallFailure ($failMsg): $tr")
            null
        }
    }
}