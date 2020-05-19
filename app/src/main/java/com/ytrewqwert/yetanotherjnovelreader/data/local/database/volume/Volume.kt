package com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ytrewqwert.yetanotherjnovelreader.common.listheader.ListHeader
import com.ytrewqwert.yetanotherjnovelreader.common.listitem.ListItem
import com.ytrewqwert.yetanotherjnovelreader.data.remote.RemoteRepository
import com.ytrewqwert.yetanotherjnovelreader.forEach
import org.json.JSONArray
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
) : ListItem, ListHeader {
    companion object {
        private fun fromJson(source: JSONObject): Volume {
            // Find the value for coverThumbUrl (if it exists)
            val attachments = source.getJSONArray("attachments")
            var coverUrl = ""
            attachments.forEach<JSONObject> {
                val attachUrl = it.getString("fullpath")
                if (attachUrl.contains("cover")) coverUrl = attachUrl
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
        fun fromJson(volumesJson: JSONArray): List<Volume> = ArrayList<Volume>().apply {
            volumesJson.forEach<JSONObject> { add(fromJson(it)) }
        }
    }

    override fun getListItemContents(): ListItem.Contents = ListItem.Contents(
        title, descriptionShort,
        "${RemoteRepository.IMG_ADDR}/$coverUrl",
        null, true
    )

    override fun getListHeaderContents(): ListHeader.Contents = ListHeader.Contents(
        "${RemoteRepository.IMG_ADDR}/$coverUrl",
        title,
        description
    )
}