package com.example.yetanotherjnovelreader.data

import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class AuthorizedStringRequest(
    private val authToken: String?,
    method: Int,
    url: String,
    listener: Response.Listener<String>,
    errorListener: Response.ErrorListener
) : StringRequest(method, url, listener, errorListener) {
    override fun getHeaders(): MutableMap<String, String> {
        val result = HashMap<String, String>()
        result["Authorization"] = authToken ?: "null"
        return result
    }
}