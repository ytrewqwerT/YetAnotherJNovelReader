package com.ytrewqwert.yetanotherjnovelreader.data.local.database

import com.ytrewqwert.yetanotherjnovelreader.data.remote.retrofit.model.UserRaw

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
        /** Converts the given [userRaw] into a [UserData]. */
        fun fromUserRaw(userRaw: UserRaw) = UserData(
            userId = userRaw.userId,
            authToken = userRaw.authToken,
            authDate = userRaw.authDate,
            username = userRaw.user.username,
            isMember = userRaw.user.currentSubscription.status == "active"
        )
    }
}