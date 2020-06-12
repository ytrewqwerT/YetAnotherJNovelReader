package com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie

import androidx.room.Embedded
import androidx.room.Relation
import com.ytrewqwert.yetanotherjnovelreader.common.listheader.ListHeader
import com.ytrewqwert.yetanotherjnovelreader.common.listitem.ListItem
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.follow.Follow

/** An aggregate class combining a [serie] with related [following] information. */
data class SerieFull(
    @Embedded val serie: Serie,
    @Relation(parentColumn = "id", entityColumn = "serieId") private val following: Follow?
) : ListItem, ListHeader {
    override fun isFollowed(): Boolean = following != null

    override fun getListItemContents(): ListItem.Contents {
        var contents = serie.getListItemContents()
        if (isFollowed()) contents = contents.copy(isFollowing = true)
        return contents
    }

    override fun getListHeaderContents(): ListHeader.Contents = serie.getListHeaderContents()
}