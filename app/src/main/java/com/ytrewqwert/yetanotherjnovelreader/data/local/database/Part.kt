package com.ytrewqwert.yetanotherjnovelreader.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject

@Entity
data class Part(
    @PrimaryKey val id: String,
    val volumeId: String,
    val serieId: String,
    val title: String,
    val titleslug: String,
    val seriesPartNum: Int,
    val coverThumbUrl: String,
    val tags: String,
    val launchDate: String,
    val expired: Boolean,
    val preview: Boolean
) {
    companion object {
        fun fromJson(source: JSONObject): Part {
            // Find the value for coverThumbUrl (if it exists)
            val attachments = source.getJSONArray("attachments")
            var thumbUrl = ""
            for (i in 0 until attachments.length()) {
                val attachUrl = attachments.getJSONObject(i).getString("fullpath")
                if (attachUrl?.contains("thumb") == true) thumbUrl = attachUrl
            }

            return Part(
                id = source.getString("id"),
                volumeId = source.getString("volumeId"),
                serieId = source.getString("serieId"),
                title = source.getString("title"),
                titleslug = source.getString("titleslug"),
                seriesPartNum = source.getInt("partNumber"),
                coverThumbUrl = thumbUrl,
                tags = source.getString("tags"),
                launchDate = source.getString("launchDate"),
                expired = source.getBoolean("expired"),
                preview = source.getBoolean("preview")
            )
        }
    }
}