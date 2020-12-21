package com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.serievolumes

import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist.SwipeableListViewModel
import com.ytrewqwert.yetanotherjnovelreader.data.FetchResult
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeFull
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SerieVolumesViewModel(
    private val repository: Repository,
    private val serieId: String
) : SwipeableListViewModel<VolumeFull>(repository) {

    override val itemsSourceFlow = repository.getSerieVolumesFlow(serieId)

    init {
        postInitialisationTasks()
        viewModelScope.launch {
            repository.getSerieFlow(serieId).collect {
                setHeader(it)
            }
        }
    }

    override suspend fun performPageFetch(amount: Int, offset: Int): FetchResult? =
        repository.fetchSerieVolumes(serieId, amount, offset)

    fun toggleFollow() {
        viewModelScope.launch {
            if (repository.isFollowed(serieId)) repository.unfollowSeries(serieId)
            else repository.followSeries(serieId)
        }
    }
}