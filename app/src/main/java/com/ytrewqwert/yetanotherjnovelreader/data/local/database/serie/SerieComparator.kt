package com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie

import androidx.recyclerview.widget.DiffUtil

object SerieComparator : DiffUtil.ItemCallback<SerieFull>() {
    override fun areItemsTheSame(oldItem: SerieFull, newItem: SerieFull): Boolean =
        oldItem.serie.id == newItem.serie.id
    override fun areContentsTheSame(oldItem: SerieFull, newItem: SerieFull): Boolean =
        oldItem == newItem
}