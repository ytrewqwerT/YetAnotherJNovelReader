package com.ytrewqwert.yetanotherjnovelreader.data.local.database.progress

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ytrewqwert.yetanotherjnovelreader.forEach
import org.json.JSONArray
import org.json.JSONObject

@Entity
data class Progress(
    @PrimaryKey val partId: String,
    val progress: Double
) {
    companion object {
        private fun fromJson(progressJson: JSONObject) =
            Progress(
                partId = progressJson.getString("partId"),
                progress = progressJson.getDouble("completion")
            )
        fun fromJson(progressesJson: JSONArray): List<Progress> = ArrayList<Progress>().apply {
            progressesJson.forEach<JSONObject> { add(fromJson(it)) }
        }
    }
}