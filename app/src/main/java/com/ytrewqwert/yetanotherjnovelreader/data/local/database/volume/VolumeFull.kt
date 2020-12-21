package com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume

import androidx.room.Embedded
import androidx.room.Relation
import com.ytrewqwert.yetanotherjnovelreader.common.listheader.ListHeader
import com.ytrewqwert.yetanotherjnovelreader.common.listitem.ListItem
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.follow.Follow
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.Part
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull

/** An aggregate class combining a [volume] with related [following] information. */
data class VolumeFull(
    @Embedded val volume: Volume,
    @Relation(parentColumn = "id", entityColumn = "volumeId", entity = Part::class) val parts: List<PartFull>,
    @Relation(parentColumn = "serieId", entityColumn = "serieId") private val following: Follow?
) : ListItem, ListHeader {
    override fun isFollowed(): Boolean = following != null

    override fun getListItemContents(): ListItem.Contents {
        var contents = volume.getListItemContents()
        if (isFollowed()) contents = contents.copy(isFollowing = true)
        return contents
    }

    override fun getListHeaderContents(): ListHeader.Contents =
        volume.getListHeaderContents().copy(mFollowing = isFollowed())
}