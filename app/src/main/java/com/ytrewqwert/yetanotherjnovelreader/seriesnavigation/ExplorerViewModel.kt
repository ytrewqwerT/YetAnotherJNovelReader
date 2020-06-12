package com.ytrewqwert.yetanotherjnovelreader.seriesnavigation

import androidx.lifecycle.ViewModel
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemViewModel
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.SerieFull
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

    // Ignore followedOnly for volumes and parts as they're already restricted to a single series.
    /** Defines a source for where to obtain data for a specific series's volumes from. */
    fun getSerieVolumesSource(serie: SerieFull): ListItemViewModel.ListItemSource {
        return ListItemViewModel.ListItemSource(
            repository.getSerieVolumesFlow(serie.serie.id)
        ) { amount, offset, _ ->
            repository.fetchSerieVolumes(serie.serie.id, amount, offset)
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