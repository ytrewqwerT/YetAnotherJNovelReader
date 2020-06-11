package com.ytrewqwert.yetanotherjnovelreader.seriesnavigation

import androidx.lifecycle.ViewModel
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemViewModel
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.SerieFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeFull

class ExplorerViewModel(private val repository: Repository) : ViewModel() {

    fun getSeriesSource(): ListItemViewModel.ListItemSource {
        return ListItemViewModel.ListItemSource(
            repository.getSeriesFlow()
        ) { amount, offset, followedOnly ->
            repository.fetchSeries(amount, offset, followedOnly)
        }
    }

    // Ignore followedOnly for volumes and parts as they're already restricted to a single series.
    fun getSerieVolumesSource(serie: SerieFull): ListItemViewModel.ListItemSource {
        return ListItemViewModel.ListItemSource(
            repository.getSerieVolumesFlow(serie.serie.id)
        ) { amount, offset, _ ->
            repository.fetchSerieVolumes(serie.serie.id, amount, offset)
        }
    }

    fun getVolumePartsSource(volume: VolumeFull): ListItemViewModel.ListItemSource {
        return ListItemViewModel.ListItemSource(
            repository.getVolumePartsFlow(volume.volume.id)
        ) { amount, offset, _ ->
            repository.fetchVolumeParts(volume.volume.id, amount, offset)
        }
    }
}