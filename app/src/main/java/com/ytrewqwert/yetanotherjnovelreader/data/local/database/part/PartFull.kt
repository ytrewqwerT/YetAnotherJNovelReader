package com.ytrewqwert.yetanotherjnovelreader.data.local.database.part

import androidx.room.Embedded
import androidx.room.Relation
import com.ytrewqwert.yetanotherjnovelreader.common.listitem.ListItem
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.follow.Follow
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.progress.Progress

/** An aggregate class combining a [part] with related [progress] and [following] information. */
data class PartFull(
    @Embedded val part: Part,
    @Relation(parentColumn = "id", entityColumn = "partId") val progress: Progress?,
    @Relation(parentColumn = "serieId", entityColumn = "serieId") private val following: Follow?
) : ListItem {
    override fun isFollowed(): Boolean = following != null

}