package com.ytrewqwert.yetanotherjnovelreader.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Follow(
    @PrimaryKey val serieId: String
)