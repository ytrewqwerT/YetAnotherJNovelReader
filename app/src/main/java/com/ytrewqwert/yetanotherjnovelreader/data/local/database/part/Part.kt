package com.ytrewqwert.yetanotherjnovelreader.data.local.database.part

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ytrewqwert.yetanotherjnovelreader.common.ListItem
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.remote.RemoteRepository
import org.json.JSONArray
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
        fun fromJson(partJson: JSONObject): Part {
            // Find the value for coverThumbUrl (if it exists)
            val attachments = partJson.getJSONArray("attachments")
            var coverUrl = ""
            for (i in 0 until attachments.length()) {
                val attachUrl = attachments.getJSONObject(i).getString("fullpath")
                if (attachUrl?.contains("cover") == true) coverUrl = attachUrl
            }

            return Part(
                id = partJson.getString("id"),
                volumeId = partJson.getString("volumeId"),
                serieId = partJson.getString("serieId"),
                title = partJson.getString("title"),
                titleslug = partJson.getString("titleslug"),
                seriesPartNum = partJson.getInt("partNumber"),
                coverUrl = coverUrl,
                tags = partJson.getString("tags"),
                launchDate = partJson.getString("launchDate"),
                expired = partJson.getBoolean("expired"),
                preview = partJson.getBoolean("preview")
            )
        }
        fun fromJson(partsJson: JSONArray): List<Part> = ArrayList<Part>().also {
            for (i in 0 until partsJson.length()) {
                it.add(
                    fromJson(
                        partsJson.getJSONObject(i)
                    )
                )
            }
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