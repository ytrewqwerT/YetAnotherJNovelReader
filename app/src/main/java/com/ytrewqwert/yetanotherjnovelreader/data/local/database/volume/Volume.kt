package com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ytrewqwert.yetanotherjnovelreader.common.listheader.ListHeader
import com.ytrewqwert.yetanotherjnovelreader.common.listitem.ListItem
import com.ytrewqwert.yetanotherjnovelreader.data.remote.RemoteRepository
import com.ytrewqwert.yetanotherjnovelreader.data.remote.model.VolumeRaw

/**
 * Contains data about a volume with ID [id].
 *
 * @property[serieId] The ID of the series that this part belongs to.
 * @property[title] The title of this part.
 * @property[titleslug] This part's title, but slugified.
 * @property[volumeNum] The volume number of this volume in the series.
 * @property[description] A description for this volume.
 * @property[descriptionShort] A shortened description for this volume.
 * @property[coverUrl] A URL identifying the source of the cover image for this part.
 * @property[tags] A comma-separated string containing relevant tags for this part.
 * @property[created] The time when this series was created.
 */
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
        /** Converts the given [volumeRaw] into a [Volume]. */
        fun fromVolumeRaw(volumeRaw: VolumeRaw): Volume {
            var coverUrl = ""
            for (attach in volumeRaw.attachments) {
                if (attach.imgUrl.contains("cover")) coverUrl = attach.imgUrl
            }

            return Volume(
                id = volumeRaw.id,
                serieId = volumeRaw.serieId,
                title = volumeRaw.title,
                titleslug = volumeRaw.titleslug,
                volumeNum = volumeRaw.volumeNumber,
                description = volumeRaw.description,
                descriptionShort = volumeRaw.descriptionShort,
                coverUrl = "${RemoteRepository.IMG_ADDR}/$coverUrl",
                tags = volumeRaw.tags,
                created = volumeRaw.created
            )
        }
    }

    override fun getListItemContents(): ListItem.Contents = ListItem.Contents(
        title, descriptionShort,
        coverUrl,
        null, true
    )

    override fun getListHeaderContents(): ListHeader.Contents = ListHeader.Contents(
        coverUrl,
        title,
        description
    )
}