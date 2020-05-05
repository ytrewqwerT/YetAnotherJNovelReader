package com.ytrewqwert.yetanotherjnovelreader.data.local.database

import org.json.JSONObject

data class UserData(
    val userId: String,
    val authToken: String,
    val authDate: String,
    val username: String,
    val isMember: Boolean
) {
    companion object {
        fun fromJson(userJson: JSONObject): UserData {
            val user = userJson.getJSONObject("user")
            val curSub = userJson.getJSONObject("currentSubscription")
            return UserData(
                userId = userJson.getString("userId"),
                authToken = userJson.getString("id"),
                authDate = userJson.getString("created"),
                username = user.getString("username"),
                isMember = curSub.getString("status") == "active"
            )
        }
    }
}