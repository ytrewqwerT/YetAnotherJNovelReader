package com.ytrewqwert.yetanotherjnovelreader.data.local.database.part

import androidx.room.Embedded
import androidx.room.Relation
import com.ytrewqwert.yetanotherjnovelreader.common.ListItem
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.follow.Follow
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.progress.Progress

data class PartFull(
    @Embedded val part: Part,
    @Relation(parentColumn = "id", entityColumn = "partId") val progress: Progress?,
    @Relation(parentColumn = "serieId", entityColumn = "serieId") private val following: Follow?
) : ListItem {
    fun isFollowed(): Boolean = following != null

    override fun getListItemContents(): ListItem.ListItemContents {
        var contents = part.getListItemContents()
        if (progress != null) contents = contents.copy(progress = progress.progress)
        if (isFollowed()) contents = contents.copy(isFollowing = true)
        return contents
    }
}