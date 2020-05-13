package com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume

import androidx.room.Embedded
import androidx.room.Relation
import com.ytrewqwert.yetanotherjnovelreader.common.ListItem
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.Follow.Follow

data class VolumeFull(
    @Embedded val volume: Volume,
    @Relation(parentColumn = "serieId", entityColumn = "serieId") private val following: Follow?
) : ListItem {
    fun isFollowed(): Boolean = following != null

    override fun getListItemContents(): ListItem.ListItemContents {
        var contents = volume.getListItemContents()
        if (isFollowed()) contents = contents.copy(isFollowing = true)
        return contents
    }
}