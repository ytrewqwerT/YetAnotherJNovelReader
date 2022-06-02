package com.ytrewqwert.yetanotherjnovelreader.data.remote

import retrofit2.Response
import retrofit2.http.*


/** A network interface for sending requests to JNC's (current) API. */
interface JNCApi {
    // Methods with return value of type Response<Void> is done to suppress retrofit's treatment of
    // 204 and other (bad) status codes as exceptions. Ideally, the caller would manually check the
    // Response's status code to ensure that it is as expected...

    /**
     * Sets the user's progress for a particular part.
     *
     * @param[authToken] A token providing authorisation to modify the user's data.
     * @param[partId] The id of the part to set the progress of.
     * @param[progress] The progress value to be set.
     */
    @PUT("me/completion/{partId}")
    suspend fun setProgress(
        @Header("Authorization") authToken: String?,
        @Path("partId", encoded = true) partId: String,
        @Body progress: Double
    ): Response<Void>

    /**
     * Sets a serie as being followed.
     *
     *
     * @param[authToken] A token providing authorisation to modify the user's data.
     * @param[serieId] The serie to be followed.
     */
    @PUT("me/follow/{serieId}")
    suspend fun followSerie(
        @Header("Authorization") authToken: String?,
        @Path("serieId", encoded = true) serieId: String
    ): Response<Void>

    /**
     * Sets a serie as not being followed.
     *
     * @param[authToken] A token providing authorisation to modify the user's data.
     * @param[serieId] The serie to be unfollowed.
     */
    @DELETE("me/follow/{serieId}")
    suspend fun unfollowSerie(
        @Header("Authorization") authToken: String?,
        @Path("serieId", encoded = true) serieId: String
    ): Response<Void>
}