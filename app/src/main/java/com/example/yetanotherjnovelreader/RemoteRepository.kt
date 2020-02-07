package com.example.yetanotherjnovelreader

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class RemoteRepository private constructor(appContext: Context) {
    companion object {
        private const val TAG = "RemoteRepository"

        @Volatile
        private var INSTANCE: RemoteRepository? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: RemoteRepository(context.applicationContext).also {
                    INSTANCE = it
                }
            }
    }

    private val requestQueue: RequestQueue by lazy { Volley.newRequestQueue(appContext) }

    fun login(email: String, password: String) {
        val args = JSONObject().put("email", email).put("password", password)

        val request = JsonObjectRequest(
            Request.Method.POST,
            "https://api.j-novel.club/api/users/login?include=user",
            args,
            Response.Listener { Log.d(TAG, "LoginSuccess: ${it.toString(4)}") },
            Response.ErrorListener { Log.d(TAG, "LoginFailure: $it") }
        )

        requestQueue.add(request)
    }

    fun getSeries(listener: (seriesList: List<Series>) -> Unit) {
        val request = JsonArrayRequest(
            Request.Method.GET,
            "https://api.j-novel.club/api/series",
            null,
            Response.Listener<JSONArray> {
                Log.d(TAG, "SeriesSuccess: Found ${it.length()} series")
                Log.d(TAG, it.toString(4))

                val resultList = ArrayList<Series>(it.length())
                for (i in 0 until it.length()) resultList.add(Series(it.getJSONObject(i)))
                listener(resultList)
            },
            Response.ErrorListener { Log.d(TAG, "SeriesFailure: $it") }
        )
        requestQueue.add(request)
    }
}
