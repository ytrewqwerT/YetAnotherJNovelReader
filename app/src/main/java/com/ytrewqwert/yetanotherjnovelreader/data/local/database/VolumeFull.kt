package com.ytrewqwert.yetanotherjnovelreader.data.local.database

import androidx.room.Embedded
import androidx.room.Relation
import com.ytrewqwert.yetanotherjnovelreader.common.ListItem

data class VolumeFull(
    @Embedded val volume: Volume,
    @Relation(parentColumn = "serieId", entityColumn = "serieId") val following: Follow?
) : ListItem {
    override fun getListItemContents(): ListItem.ListItemContents {
        var contents = volume.getListItemContents()
        if (following != null) contents = contents.copy(isFollowing = true)
        return contents
    }
}