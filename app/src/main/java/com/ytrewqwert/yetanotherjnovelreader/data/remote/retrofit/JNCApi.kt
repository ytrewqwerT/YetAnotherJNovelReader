package com.ytrewqwert.yetanotherjnovelreader.data.remote.retrofit

import com.ytrewqwert.yetanotherjnovelreader.data.remote.retrofit.model.*
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

    // Authorized requests...

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
}