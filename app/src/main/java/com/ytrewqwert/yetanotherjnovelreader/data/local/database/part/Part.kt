package com.ytrewqwert.yetanotherjnovelreader.data.local.database.part

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ytrewqwert.yetanotherjnovelreader.common.listitem.ListItem
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.remote.RemoteRepository
import com.ytrewqwert.yetanotherjnovelreader.forEach
import org.json.JSONArray
import org.json.JSONObject

/**
 * Contains data about a part with ID [id].
 *
 * @property[volumeId] The ID of the volume that this part belongs to.
 * @property[serieId] The ID of the series that this part belongs to.
 * @property[title] The title of this part.
 * @property[titleslug] This part's title, but slugified.
 * @property[seriesPartNum] The part number of this part in the series.
 * @property[coverUrl] A URL identifying the source of the cover image for this part.
 * @property[tags] A comma-separated string containing relevant tags for this part.
 * @property[launchDate] The time when this part was released.
 * @property[expired] Whether this part has expired or not.
 * @property[preview] Whether this part is a preview part that can be viewed by non-subscribers.
 */
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
        /** Converts the given [partJson] into a [Part]. */
        fun fromJson(partJson: JSONObject): Part {
            // Find the value for coverThumbUrl (if it exists)
            val attachments = partJson.getJSONArray("attachments")
            var coverUrl = ""
            for (i in 0 until attachments.length()) {
                val attachUrl = attachments.getJSONObject(i).getString("fullpath")
                if (attachUrl.contains("cover")) coverUrl = attachUrl
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
        /** Converts the given [partsJson] into a list of [Part]. */
        fun fromJson(partsJson: JSONArray): List<Part> = ArrayList<Part>().apply {
            partsJson.forEach<JSONObject> { add(fromJson(it)) }
        }
    }

    override fun getListItemContents(): ListItem.Contents = ListItem.Contents(
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