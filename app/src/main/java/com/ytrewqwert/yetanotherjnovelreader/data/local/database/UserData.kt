package com.ytrewqwert.yetanotherjnovelreader.data.local.database

import org.json.JSONObject

/**
 * Contains some data pertaining to a user's J-Novel Club account.
 *
 * @property[userId] The user's ID.
 * @property[authToken] An authentication token providing user privileges.
 * @property[authDate] The time in which the authToken was generated.
 * @property[username] The user's username.
 * @property[isMember] Whether the user is a paying subscriber to the service.
 */
data class UserData(
    val userId: String,
    val authToken: String,
    val authDate: String,
    val username: String,
    val isMember: Boolean
) {
    companion object {
        /** Converts the given [userJson] into a [UserData]. */
        fun fromJson(userJson: JSONObject): UserData {
            val user = userJson.getJSONObject("user")
            val curSub = user.getJSONObject("currentSubscription")
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