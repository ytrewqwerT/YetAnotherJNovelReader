package com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ytrewqwert.yetanotherjnovelreader.common.ListItem
import com.ytrewqwert.yetanotherjnovelreader.data.remote.RemoteRepository
import org.json.JSONArray
import org.json.JSONObject

@Entity
data class Serie(
    @PrimaryKey val id: String,
    val title: String,
    val titleslug: String,
    val description: String,
    val descriptionShort: String,
    val coverUrl: String,
    val tags: String,
    val created: String,
    val overrideExpiration: Boolean
) : ListItem {
    companion object {
        fun fromJson(serieJson: JSONObject): Serie {
            // Find the value for coverThumbUrl (if it exists)
            val attachments = serieJson.getJSONArray("attachments")
            var coverUrl = ""
            for (i in 0 until attachments.length()) {
                val attachUrl = attachments.getJSONObject(i).getString("fullpath")
                if (attachUrl?.contains("cover") == true) coverUrl = attachUrl
            }

            return Serie(
                id = serieJson.getString("id"),
                title = serieJson.getString("title"),
                titleslug = serieJson.getString("titleslug"),
                description = serieJson.getString("description"),
                descriptionShort = serieJson.getString("descriptionShort"),
                coverUrl = coverUrl,
                tags = serieJson.getString("tags"),
                created = serieJson.getString("created"),
                overrideExpiration = serieJson.getBoolean("override_expiration")
            )
        }
        fun fromJson(seriesJson: JSONArray): List<Serie> = ArrayList<Serie>().also {
            for (i in 0 until seriesJson.length()) {
                it.add(
                    fromJson(
                        seriesJson.getJSONObject(i)
                    )
                )
            }
        }
    }

    override fun getListItemContents(): ListItem.ListItemContents = ListItem.ListItemContents(
        title, descriptionShort,
        "${RemoteRepository.IMG_ADDR}/$coverUrl",
        null, true
    )
}