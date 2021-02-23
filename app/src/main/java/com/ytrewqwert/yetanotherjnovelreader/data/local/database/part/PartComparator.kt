package com.ytrewqwert.yetanotherjnovelreader.data.local.database.part

import androidx.recyclerview.widget.DiffUtil

object PartComparator : DiffUtil.ItemCallback<PartFull>() {
    override fun areItemsTheSame(oldItem: PartFull, newItem: PartFull): Boolean =
        oldItem.part.id == newItem.part.id
    override fun areContentsTheSame(oldItem: PartFull, newItem: PartFull): Boolean =
        oldItem == newItem
}