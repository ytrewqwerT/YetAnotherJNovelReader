package com.example.yetanotherjnovelreader

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class RemoteRepository private constructor(appContext: Context) {
    companion object {
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
            Response.Listener { Log.d("Nice", "LoginSuccess: ${it.toString(4)}") },
            Response.ErrorListener { Log.d("Nice", "LoginFailure: $it") }
        )

        requestQueue.add(request)
    }
}
