package com.ytrewqwert.yetanotherjnovelreader.data.remote.retrofit

import com.ytrewqwert.yetanotherjnovelreader.data.remote.retrofit.model.*
import retrofit2.Response
import retrofit2.http.*

interface JNCApi {
    @GET("api/series")
    suspend fun getSeries(@Query("filter") params: Any? = null): List<SerieRaw>

    @GET("api/volumes")
    suspend fun getVolumes(@Query("filter") params: Any? = null): List<VolumeRaw>

    @GET("api/parts")
    suspend fun getParts(@Query("filter") params: Any? = null): List<PartRaw>

    @GET("api/parts/findOne")
    suspend fun getPart(@Query("filter") params: Any): PartRaw

    @GET("api/users/{userId}")
    suspend fun getUser(
        @Header("Authorization") authToken: String?,
        @Path("userId") userId: String,
        @Query("filter") params: Any
    ): UserRaw

    @POST("api/users/{userId}/updateReadCompletion")
    suspend fun setProgress(
        @Header("Authorization") authToken: String?,
        @Path("userId") userId: String,
        @Body progress: ProgressRaw
    )

    @POST("api/users/login?include=user")
    suspend fun login(@Body credentials: LoginRaw): UserRaw

    @POST("api/users/logout")
    suspend fun logout(@Header("Authorization") authToken: String?): Response<Void>

    @GET("api/parts/{partId}/partData")
    suspend fun getPartHtml(
        @Header("Authorization") authToken: String?,
        @Path("partId") partId: String
    ): PartContentRaw
}