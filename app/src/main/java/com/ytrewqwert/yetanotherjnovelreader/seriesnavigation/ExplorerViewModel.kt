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
        ) { _, amount, offset ->
            repository.fetchSeries(amount, offset)
        }
    }

    fun getSerieVolumesSource(serie: SerieFull): ListItemViewModel.ListItemSource {
        return ListItemViewModel.ListItemSource(
            repository.getSerieVolumesFlow(serie.serie.id)
        ) { _, amount, offset ->
            repository.fetchSerieVolumes(serie.serie.id, amount, offset)
        }
    }

    fun getVolumePartsSource(volume: VolumeFull): ListItemViewModel.ListItemSource {
        return ListItemViewModel.ListItemSource(
            repository.getVolumePartsFlow(volume.volume.id)
        ) { _, amount, offset ->
            repository.fetchVolumeParts(volume.volume.id, amount, offset)
        }
    }
}