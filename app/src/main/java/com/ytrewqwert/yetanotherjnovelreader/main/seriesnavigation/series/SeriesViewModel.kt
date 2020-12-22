package com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.series

import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist.SwipeableListViewModel
import com.ytrewqwert.yetanotherjnovelreader.data.FetchResult
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.SerieFull
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SeriesViewModel(
    private val repository: Repository
): SwipeableListViewModel<SerieFull>(repository) {

    override val itemsSourceFlow = repository.getSeriesFlow()
        .combine(repository.isFilterFollowing) { series, filterOn ->
            series.filter { !filterOn || it.isFollowed() }
        }

    private var isFilterFollowing = false

    init {
        viewModelScope.launch {
            repository.isFilterFollowing.collect { isFilterFollowing = it }
        }
        postInitialisationTasks()
    }

    override suspend fun performPageFetch(amount: Int, offset: Int): FetchResult? =
        repository.fetchSeries(amount, offset, isFilterFollowing)

    fun toggleFollow(serie: SerieFull) {
        val following: Boolean = serie.isFollowed()
        val serieId = serie.serie.id
        viewModelScope.launch {
            if (following) repository.unfollowSeries(serieId)
            else repository.followSeries(serieId)
        }
    }
}