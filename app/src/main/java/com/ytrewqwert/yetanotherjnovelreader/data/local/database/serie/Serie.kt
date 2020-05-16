package com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ytrewqwert.yetanotherjnovelreader.common.ListItem
import com.ytrewqwert.yetanotherjnovelreader.data.remote.RemoteRepository
import com.ytrewqwert.yetanotherjnovelreader.forEach
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
        private fun fromJson(serieJson: JSONObject): Serie {
            // Find the value for coverThumbUrl (if it exists)
            val attachments = serieJson.getJSONArray("attachments")
            var coverUrl = ""
            attachments.forEach<JSONObject> {
                val attachUrl = it.getString("fullpath")
                if (attachUrl.contains("cover")) coverUrl = attachUrl
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
        fun fromJson(seriesJson: JSONArray): List<Serie> = ArrayList<Serie>().apply {
            seriesJson.forEach<JSONObject> { add(fromJson(it)) }
        }
    }

    override fun getListItemContents(): ListItem.ListItemContents = ListItem.ListItemContents(
        title, descriptionShort,
        "${RemoteRepository.IMG_ADDR}/$coverUrl",
        null, true
    )
}