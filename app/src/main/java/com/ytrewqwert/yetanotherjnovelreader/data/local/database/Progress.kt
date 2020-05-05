package com.ytrewqwert.yetanotherjnovelreader.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject

@Entity
data class Progress(
    @PrimaryKey val partId: String,
    val progress: Double
) {
    companion object {
        fun fromJson(progressJson: JSONObject) = Progress(
            partId = progressJson.getString("partId"),
            progress = progressJson.getDouble("completion")
        )
        fun fromJson(progressesJson: JSONArray): List<Progress> = ArrayList<Progress>().also {
            for (i in 0 until progressesJson.length()) {
                it.add(fromJson(progressesJson.getJSONObject(i)))
            }
        }
    }
}