package com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume

import androidx.recyclerview.widget.DiffUtil

object VolumeComparator : DiffUtil.ItemCallback<VolumeFull>() {
    override fun areItemsTheSame(oldItem: VolumeFull, newItem: VolumeFull): Boolean =
        oldItem.volume.id == newItem.volume.id
    override fun areContentsTheSame(oldItem: VolumeFull, newItem: VolumeFull): Boolean =
        oldItem == newItem
}