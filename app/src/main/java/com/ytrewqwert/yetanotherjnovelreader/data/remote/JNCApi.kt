package com.ytrewqwert.yetanotherjnovelreader.data.remote

import com.ytrewqwert.yetanotherjnovelreader.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

/** A network interface for sending requests to JNC's API. */
interface JNCApi {
    /**
     * Retrieves a list of series.
     *
     * @param[params] A filter for which series to retrieve.
     */
    @GET("api/series")
    suspend fun getSeries(
        @Query("filter", encoded = true) params: Any? = null
    ): List<SerieRaw>

    /**
     * Retrieves a list of volumes.
     *
     * @param[params] A filter for which volumes to retrieve.
     */
    @GET("api/volumes")
    suspend fun getVolumes(
        @Query("filter", encoded = true) params: Any? = null
    ): List<VolumeRaw>

    /**
     * Retrieves a list of parts.
     *
     * @param[params] A filter for which parts to retrieve.
     */
    @GET("api/parts")
    suspend fun getParts(
        @Query("filter", encoded = true) params: Any? = null
    ): List<PartRaw>

    /**
     * Retrieves a single part.
     *
     * @param[params] A filter for which part to retrieve.
     */
    @GET("api/parts/findOne")
    suspend fun getPart(
        @Query("filter", encoded = true) params: Any
    ): PartRaw

    /**
     * Retrieves information about a user.
     *
     * @param[authToken] A token providing authorisation to retrieve the requested content.
     * @param[userId] The id of the user to retrieve.
     * @param[params] A filter containing additional information for the request.
     */
    @GET("api/users/{userId}")
    suspend fun getUser(
        @Header("Authorization") authToken: String?,
        @Path("userId", encoded = true) userId: String,
        @Query("filter", encoded = true) params: Any
    ): UserRaw

    /**
     * Sets the user's progress for a particular part.
     *
     * @param[authToken] A token providing authorisation to modify the user's data.
     * @param[userId] The user's id.
     * @param[progress] The progress data to be set for the user.
     */
    @POST("api/users/{userId}/updateReadCompletion")
    suspend fun setProgress(
        @Header("Authorization") authToken: String?,
        @Path("userId", encoded = true) userId: String,
        @Body progress: ProgressRaw
    )

    /**
     * Sets a serie as being followed.
     *
     * @param[authToken] A token providing authorisation to modify the user's data.
     * @param[userId] The user's id.
     * @param[follow] The serie to be followed.
     */
    @POST("api/users/{userId}/follow")
    suspend fun followSerie(
        @Header("Authorization") authToken: String?,
        @Path("userId", encoded = true) userId: String,
        @Body follow: OutboundFollowRaw
    )

    /**
     * Sets a serie as not being followed.
     *
     * @param[authToken] A token providing authorisation to modify the user's data.
     * @param[userId] The user's id.
     * @param[follow] The serie to be unfollowed.
     */
    @POST("api/users/{userId}/unfollow")
    suspend fun unfollowSerie(
        @Header("Authorization") authToken: String?,
        @Path("userId", encoded = true) userId: String,
        @Body follow: OutboundFollowRaw
    )

    /** Logs in using the provided [credentials], returning the logged-in user's data. */
    @POST("api/users/login?include=user")
    suspend fun login(
        @Body credentials: LoginRaw
    ): UserRaw

    /** Logs out, invalidating the given [authToken]. */
    @POST("api/users/logout")
    suspend fun logout(
        @Header("Authorization") authToken: String?
    ): Response<Void>

    /**
     * Retrieves the contents of a particular part.
     *
     * @param[authToken] An authorisation token allowing the caller to retrieve the content.
     * @param[partId] The id of the part to retrieve the content of.
     */
    @GET("api/parts/{partId}/partData")
    suspend fun getPartHtml(
        @Header("Authorization") authToken: String?,
        @Path("partId", encoded = true) partId: String
    ): PartContentRaw
}