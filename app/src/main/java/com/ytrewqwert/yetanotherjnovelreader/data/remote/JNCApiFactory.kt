package com.ytrewqwert.yetanotherjnovelreader.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** A factory object from which instances of [OldJNCApi] and [JNCApi] can be retrieved. */
object JNCApiFactory {
    private const val oldJNCApiAddr = "https://api.j-novel.club/"
    private const val JNCApiAddr = "https://labs.j-novel.club/app/v1/"

    private val retrofitOldApi = Retrofit.Builder()
        .baseUrl(oldJNCApiAddr)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val retrofitNewApi = Retrofit.Builder()
        .baseUrl(JNCApiAddr)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val OLD_JNC_API: OldJNCApi = retrofitOldApi.create(OldJNCApi::class.java)
    val NEW_JNC_API: JNCApi = retrofitNewApi.create(JNCApi::class.java)
}