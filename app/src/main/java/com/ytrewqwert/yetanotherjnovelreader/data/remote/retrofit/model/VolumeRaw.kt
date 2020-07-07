package com.ytrewqwert.yetanotherjnovelreader.data.remote.retrofit.model

data class VolumeRaw(
    val id: String,
    val serieId: String,
    val title: String,
    val titleslug: String,
    val volumeNumber: Int,
    val description: String,
    val descriptionShort: String,
    val attachments: List<AttachRaw>,
    val tags: String,
    val created: String
)