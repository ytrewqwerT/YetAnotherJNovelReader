package com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ytrewqwert.yetanotherjnovelreader.common.listheader.ListHeader
import com.ytrewqwert.yetanotherjnovelreader.common.listitem.ListItem
import com.ytrewqwert.yetanotherjnovelreader.data.remote.RemoteRepository
import com.ytrewqwert.yetanotherjnovelreader.data.remote.model.SerieRaw

/**
 * Contains data about a series with ID [id].
 *
 * @property[title] The title of this part.
 * @property[titleslug] This part's title, but slugified.
 * @property[description] A description for this series.
 * @property[descriptionShort] A shortened description for this series.
 * @property[coverUrl] A URL identifying the source of the cover image for this part.
 * @property[tags] A comma-separated string containing relevant tags for this part.
 * @property[created] The time when this series was created.
 * @property[overrideExpiration] Whether parts in this series's expiration status should be ignored.
 */
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
) : ListItem, ListHeader {
    companion object {
        /** Converts the given [serieRaw] into a [Serie]. */
        fun fromSerieRaw(serieRaw: SerieRaw): Serie {
            var coverUrl = ""
            for (attach in serieRaw.attachments) {
                if (attach.imgUrl.contains("cover")) coverUrl = attach.imgUrl
            }

            return Serie(
                id = serieRaw.id,
                title = serieRaw.title,
                titleslug = serieRaw.titleslug,
                description = serieRaw.description,
                descriptionShort = serieRaw.descriptionShort,
                coverUrl = coverUrl,
                tags = serieRaw.tags,
                created = serieRaw.created,
                overrideExpiration = serieRaw.overrideExpiration
            )
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