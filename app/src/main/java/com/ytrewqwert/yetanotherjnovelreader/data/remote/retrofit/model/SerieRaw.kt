package com.ytrewqwert.yetanotherjnovelreader.data.remote.retrofit.model

import com.google.gson.annotations.SerializedName

data class SerieRaw(
    val id: String,
    val title: String,
    val titleslug: String,
    val description: String,
    val descriptionShort: String,
    val attachments: List<AttachRaw>,
    val tags: String,
    val created: String,
    @SerializedName("override_expiration") val overrideExpiration: Boolean
)