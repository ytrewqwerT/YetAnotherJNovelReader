package com.ytrewqwert.yetanotherjnovelreader.data.local.database

import androidx.room.Embedded
import androidx.room.Relation
import com.ytrewqwert.yetanotherjnovelreader.common.ListItem

data class PartWithProgress(
    @Embedded val part: Part,
    @Relation(parentColumn = "id", entityColumn = "partId") val progress: Progress?
) : ListItem {
    override fun getListItemContents(): ListItem.ListItemContents {
        if (progress != null) return part.getListItemContents().copy(progress = progress.progress)
        return part.getListItemContents()
    }
}