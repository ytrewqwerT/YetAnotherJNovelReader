package com.ytrewqwert.yetanotherjnovelreader.data.local.database.progress

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ytrewqwert.yetanotherjnovelreader.forEach
import org.json.JSONArray
import org.json.JSONObject

/**
 * Identifies how much progress the user has made through the part with ID [partId].
 *
 * @property[progress] How far the user has progressed through this part, between 0 and 1.
 */
@Entity
data class Progress(
    @PrimaryKey val partId: String,
    val progress: Double
) {
    companion object {
        /** Converts the given [progressJson] into a [Progress]. */
        private fun fromJson(progressJson: JSONObject) = Progress(
            partId = progressJson.getString("partId"),
            progress = progressJson.getDouble("completion")
        )

        /** Converts the given [progressesJson] into a list of [Progress]. */
        fun fromJson(progressesJson: JSONArray): List<Progress> = ArrayList<Progress>().apply {
            progressesJson.forEach<JSONObject> { add(fromJson(it)) }
        }
    }
}