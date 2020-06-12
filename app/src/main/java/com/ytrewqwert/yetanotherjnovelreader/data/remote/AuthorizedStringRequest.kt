package com.ytrewqwert.yetanotherjnovelreader.data.remote

import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

/**
 * A [StringRequest], but with an added Authorization header.
 *
 * @property[authToken] The value to associate with the Authorization header.
 */
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