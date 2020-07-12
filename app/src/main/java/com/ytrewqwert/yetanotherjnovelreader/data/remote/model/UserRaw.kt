package com.ytrewqwert.yetanotherjnovelreader.data.remote.model

import com.google.gson.annotations.SerializedName

data class UserRaw(
    val user: UserInfoRaw,
    val userId: String,
    @SerializedName("id") val authToken: String,
    @SerializedName("created") val authDate: String,
    val readParts: List<ProgressRaw>?
) {
    data class UserInfoRaw(
        val username: String,
        val currentSubscription: SubscriptionRaw
    ) {
        data class SubscriptionRaw(val status: String)
    }
}