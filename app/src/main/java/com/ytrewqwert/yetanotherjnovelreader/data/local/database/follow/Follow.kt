package com.ytrewqwert.yetanotherjnovelreader.data.local.database.follow

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Follow(
    @PrimaryKey val serieId: String,
    val nextPartNum: Int
)