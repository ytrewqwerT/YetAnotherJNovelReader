package com.ytrewqwert.yetanotherjnovelreader.data.remote.retrofit

import com.ytrewqwert.yetanotherjnovelreader.data.remote.retrofit.model.PartRaw
import com.ytrewqwert.yetanotherjnovelreader.data.remote.retrofit.model.SerieRaw
import com.ytrewqwert.yetanotherjnovelreader.data.remote.retrofit.model.VolumeRaw
import retrofit2.http.GET
import retrofit2.http.Query

interface JNCApi {
    @GET("api/series")
    suspend fun getSeries(@Query("filter") params: Any? = null): List<SerieRaw>

    @GET("api/volumes")
    suspend fun getVolumes(@Query("filter") params: Any? = null): List<VolumeRaw>

    @GET("api/parts")
    suspend fun getParts(@Query("filter") params: Any? = null): List<PartRaw>

    @GET("api/parts/findOne")
    suspend fun getPart(@Query("filter") params: Any): PartRaw
}