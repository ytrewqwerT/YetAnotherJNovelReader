package com.ytrewqwert.yetanotherjnovelreader.data.local.database

import androidx.room.Embedded
import androidx.room.Relation

data class PartWithProgress(
    @Embedded val part: Part,
    @Relation(parentColumn = "id", entityColumn = "partId") val progress: Progress?
)