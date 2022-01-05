package com.ytrewqwert.yetanotherjnovelreader.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** A factory object from which instances of [JNCApiNew] can be retrieved. */
object JNCApiNewFactory {
    private const val JNCApiAddr = "https://labs.j-novel.club/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(JNCApiAddr)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val jncApi: JNCApiNew = retrofit.create(JNCApiNew::class.java)
}