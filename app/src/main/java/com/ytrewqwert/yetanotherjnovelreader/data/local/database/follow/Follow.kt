package com.ytrewqwert.yetanotherjnovelreader.data.local.database.follow

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Identifies the series with ID [serieId] as being followed by the user.
 *
 * @property[nextPartNum] The next part number in the series for the user to read.
 */
@Entity
data class Follow(
    @PrimaryKey val serieId: String,
    val nextPartNum: Int
)