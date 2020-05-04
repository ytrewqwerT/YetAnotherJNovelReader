package com.ytrewqwert.yetanotherjnovelreader.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ytrewqwert.yetanotherjnovelreader.common.ListItem
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.remote.RemoteRepository
import org.json.JSONObject

@Entity
data class Part(
    @PrimaryKey val id: String,
    val volumeId: String,
    val serieId: String,
    val title: String,
    val titleslug: String,
    val seriesPartNum: Int,
    val coverUrl: String,
    val tags: String,
    val launchDate: String,
    val expired: Boolean,
    val preview: Boolean
) : ListItem {
    companion object {
        fun fromJson(source: JSONObject): Part {
            // Find the value for coverThumbUrl (if it exists)
            val attachments = source.getJSONArray("attachments")
            var coverUrl = ""
            for (i in 0 until attachments.length()) {
                val attachUrl = attachments.getJSONObject(i).getString("fullpath")
                if (attachUrl?.contains("cover") == true) coverUrl = attachUrl
            }

            return Part(
                id = source.getString("id"),
                volumeId = source.getString("volumeId"),
                serieId = source.getString("serieId"),
                title = source.getString("title"),
                titleslug = source.getString("titleslug"),
                seriesPartNum = source.getInt("partNumber"),
                coverUrl = coverUrl,
                tags = source.getString("tags"),
                launchDate = source.getString("launchDate"),
                expired = source.getBoolean("expired"),
                preview = source.getBoolean("preview")
            )
        }
    }

    override fun getListItemContents(): ListItem.ListItemContents = ListItem.ListItemContents(
        title, null,
        "${RemoteRepository.IMG_ADDR}/$coverUrl",
        null, readable()
    )

    private fun readable(): Boolean {
        if (expired) return false
        if (preview) return true
        return Repository.getInstance()?.isMember() ?: false
    }
}