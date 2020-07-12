package com.ytrewqwert.yetanotherjnovelreader.data.local.database.part

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ytrewqwert.yetanotherjnovelreader.common.listitem.ListItem
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.remote.RemoteRepository
import com.ytrewqwert.yetanotherjnovelreader.data.remote.model.PartRaw
import java.util.*

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
        /** Converts the given [partRaw] into a [Part]. */
        fun fromPartRaw(partRaw: PartRaw): Part {
            var coverUrl = ""
            for (attach in partRaw.attachments) {
                if (attach.imgUrl.contains("cover")) coverUrl = attach.imgUrl
            }

            return Part(
                id = partRaw.id,
                volumeId = partRaw.volumeId,
                serieId = partRaw.serieId,
                title = partRaw.title,
                titleslug = partRaw.titleslug,
                seriesPartNum = partRaw.partNumber,
                coverUrl = coverUrl,
                tags = partRaw.tags,
                launchDate = partRaw.launchDate,
                expired = partRaw.expired,
                preview = partRaw.preview
            )
        }
    }

    override fun getListItemContents(): ListItem.Contents = ListItem.Contents(
        title, null,
        "${RemoteRepository.IMG_ADDR}/$coverUrl",
        null, readable()
    )

    override fun hasTerm(term: String): Boolean {
        val lowerTerm = term.toLowerCase(Locale.ROOT)
        if (title.toLowerCase(Locale.ROOT).contains(lowerTerm)) return true
        if (tags.toLowerCase(Locale.ROOT).contains(lowerTerm)) return true
        return super.hasTerm(term)
    }

    private fun readable(): Boolean {
        if (expired) return false
        if (preview) return true
        return Repository.getInstance()?.isMember() ?: false
    }
}