package com.ytrewqwert.yetanotherjnovelreader.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** A factory object from which instances of [JNCApi] can be retrieved. */
object JNCApiFactory {
    private const val JNCApiAddr = "https://api.j-novel.club/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(JNCApiAddr)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val jncApi: JNCApi = retrofit.create(
        JNCApi::class.java)
}