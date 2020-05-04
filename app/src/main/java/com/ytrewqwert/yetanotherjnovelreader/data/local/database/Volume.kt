package com.ytrewqwert.yetanotherjnovelreader.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ytrewqwert.yetanotherjnovelreader.common.ListItem
import com.ytrewqwert.yetanotherjnovelreader.data.remote.RemoteRepository
import org.json.JSONObject

@Entity
data class Volume(
    @PrimaryKey val id: String,
    val serieId: String,
    val title: String,
    val titleslug: String,
    val volumeNum: Int,
    val description: String,
    val descriptionShort: String,
    val coverUrl: String,
    val tags: String,
    val created: String
) : ListItem {
    companion object {
        fun fromJson(source: JSONObject): Volume {
            // Find the value for coverThumbUrl (if it exists)
            val attachments = source.getJSONArray("attachments")
            var coverUrl = ""
            for (i in 0 until attachments.length()) {
                val attachUrl = attachments.getJSONObject(i).getString("fullpath")
                if (attachUrl?.contains("cover") == true) coverUrl = attachUrl
            }

            return Volume(
                id = source.getString("id"),
                serieId = source.getString("serieId"),
                title = source.getString("title"),
                titleslug = source.getString("titleslug"),
                volumeNum = source.getInt("volumeNumber"),
                description = source.getString("description"),
                descriptionShort = source.getString("descriptionShort"),
                coverUrl = coverUrl,
                tags = source.getString("tags"),
                created = source.getString("created")
            )
        }
    }

    override fun getListItemContents(): ListItem.ListItemContents = ListItem.ListItemContents(
        title, descriptionShort,
        "${RemoteRepository.IMG_ADDR}/$coverUrl",
        null, true
    )
}