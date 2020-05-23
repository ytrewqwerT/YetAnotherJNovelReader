package com.ytrewqwert.yetanotherjnovelreader.seriesnavigation

import androidx.lifecycle.ViewModel
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemViewModel
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.SerieFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeFull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

class ExplorerViewModel(private val repository: Repository) : ViewModel() {

    @ExperimentalCoroutinesApi
    fun getSeriesSource(): ListItemViewModel.ListItemSource {
        val filteredSeriesFlow = repository.getSeriesFlow()
            .combine(repository.isFilterFollowing) { series, filterOn ->
                series.filter { !filterOn || it.isFollowed() }
            }.distinctUntilChanged()
        return ListItemViewModel.ListItemSource(filteredSeriesFlow) { _, amount, offset ->
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