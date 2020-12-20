package com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation

import androidx.lifecycle.ViewModel
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemViewModel
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeFull

/** Exposes the data to be shown in each page of an [ExplorerFragment]. */
class ExplorerViewModel(private val repository: Repository) : ViewModel() {
    /** Defines a source for where to obtain data for available series from. */
    fun getSeriesSource(): ListItemViewModel.ListItemSource {
        return ListItemViewModel.ListItemSource(
            repository.getSeriesFlow()
        ) { amount, offset, followedOnly ->
            repository.fetchSeries(amount, offset, followedOnly)
        }
    }

    /** Defines a source for where to obtain data for a specific volume's parts from. */
    fun getVolumePartsSource(volume: VolumeFull): ListItemViewModel.ListItemSource {
        return ListItemViewModel.ListItemSource(
            repository.getVolumePartsFlow(volume.volume.id)
        ) { amount, offset, _ ->
            repository.fetchVolumeParts(volume.volume.id, amount, offset)
        }
    }
}