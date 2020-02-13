package com.example.yetanotherjnovelreader.data.remote

import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class AuthorizedJsonObjectRequest(
    private val authToken: String?,
    method: Int,
    url: String,
    jsonRequest: JSONObject?,
    listener: Response.Listener<JSONObject>,
    errorListener: Response.ErrorListener
) : JsonObjectRequest(method, url, jsonRequest, listener, errorListener) {
    override fun getHeaders(): MutableMap<String, String> {
        val result = HashMap<String, String>()
        result["Authorization"] = authToken ?: "null"
        return result
    }
}