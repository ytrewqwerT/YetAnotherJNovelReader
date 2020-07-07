package com.ytrewqwert.yetanotherjnovelreader.data.remote.retrofit

import com.ytrewqwert.yetanotherjnovelreader.data.remote.retrofit.model.SerieRaw
import retrofit2.http.GET
import retrofit2.http.Query

interface JNCApi {
    @GET("api/series")
    suspend fun getSeries(@Query("filter") params: Any? = null): List<SerieRaw>
}