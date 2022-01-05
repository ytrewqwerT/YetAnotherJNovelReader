package com.ytrewqwert.yetanotherjnovelreader.data.remote

import com.ytrewqwert.yetanotherjnovelreader.data.remote.model.PartContentRaw
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface JNCApiNew {
    /**
     * Retrieves the contents of a particular part.
     *
     * @param[authToken] An authorisation token allowing the caller to retrieve the content.
     * @param[partId] The id of the part to retrieve the content of.
     */
    @GET("app/v1/parts/{partId}/data?format=json")
    suspend fun getPartHtml(
        @Header("Authorization") authToken: String?,
        @Path("partId", encoded = true) partId: String
    ): PartContentRaw
}