package com.ytrewqwert.yetanotherjnovelreader.data.remote.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object JNCApiFactory {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.j-novel.club/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val jncApi: JNCApi = retrofit.create(JNCApi::class.java)
}