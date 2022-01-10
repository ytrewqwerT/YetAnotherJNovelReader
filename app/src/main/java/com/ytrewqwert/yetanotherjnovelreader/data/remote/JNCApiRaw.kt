package com.ytrewqwert.yetanotherjnovelreader.data.remote

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Object for JNC API calls executed outside of Retrofit. */
object JNCApiRaw {
    /**
     * Retrieves the contents of a particular part.
     *
     * @param[authToken] An authorisation token allowing the caller to retrieve the content.
     * @param[partId] The id of the part to retrieve the content of.
     */
    suspend fun getPartHtml(
        authToken: String?, partId: String
    ): String? = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://labs.j-novel.club/embed/$partId/data.xhtml")
            .addHeader("Authorization", "Bearer $authToken")
            .build()
        val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
        val responseBody = response.body().string()
        responseBody
    }
}