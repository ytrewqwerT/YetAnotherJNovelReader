package com.ytrewqwert.yetanotherjnovelreader.data.local.database

import androidx.room.Embedded
import androidx.room.Relation
import com.ytrewqwert.yetanotherjnovelreader.common.ListItem

data class PartFull(
    @Embedded val part: Part,
    @Relation(parentColumn = "id", entityColumn = "partId") val progress: Progress?,
    @Relation(parentColumn = "serieId", entityColumn = "serieId") val following: Follow?
) : ListItem {
    override fun getListItemContents(): ListItem.ListItemContents {
        var contents = part.getListItemContents()
        if (progress != null) contents = contents.copy(progress = progress.progress)
        if (following != null) contents = contents.copy(isFollowing = true)
        return contents
    }
}