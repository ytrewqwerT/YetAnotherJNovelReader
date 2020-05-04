package com.ytrewqwert.yetanotherjnovelreader.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject

@Entity
data class Progress(
    @PrimaryKey val partId: String,
    val progress: Double
) {
    companion object {
        fun fromJson(source: JSONObject): Progress {
            return Progress(
                partId = source.getString("partId"),
                progress = source.getDouble("completion")
            )
        }
    }
}