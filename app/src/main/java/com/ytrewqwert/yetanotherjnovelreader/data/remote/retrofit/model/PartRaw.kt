package com.ytrewqwert.yetanotherjnovelreader.data.remote.retrofit.model

data class PartRaw(
    val id: String,
    val volumeId: String,
    val serieId: String,
    val title: String,
    val titleslug: String,
    val partNumber: Int,
    val attachments: List<AttachRaw>,
    val tags: String,
    val launchDate: String,
    val expired: Boolean,
    val preview: Boolean
)