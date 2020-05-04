package com.ytrewqwert.yetanotherjnovelreader.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ytrewqwert.yetanotherjnovelreader.common.ListItem
import com.ytrewqwert.yetanotherjnovelreader.data.remote.RemoteRepository
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
        fun fromJson(source: JSONObject): Serie {
            // Find the value for coverThumbUrl (if it exists)
            val attachments = source.getJSONArray("attachments")
            var coverUrl = ""
            for (i in 0 until attachments.length()) {
                val attachUrl = attachments.getJSONObject(i).getString("fullpath")
                if (attachUrl?.contains("cover") == true) coverUrl = attachUrl
            }

            return Serie(
                id = source.getString("id"),
                title = source.getString("title"),
                titleslug = source.getString("titleslug"),
                description = source.getString("description"),
                descriptionShort = source.getString("descriptionShort"),
                coverUrl = coverUrl,
                tags = source.getString("tags"),
                created = source.getString("created"),
                overrideExpiration = source.getBoolean("override_expiration")
            )
        }
    }

    override fun getListItemContents(): ListItem.ListItemContents = ListItem.ListItemContents(
        title, descriptionShort,
        "${RemoteRepository.IMG_ADDR}/$coverUrl",
        null, true
    )
}