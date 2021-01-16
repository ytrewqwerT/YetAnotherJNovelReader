package com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.serieslist

import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist.SwipeableListViewModel
import com.ytrewqwert.yetanotherjnovelreader.data.FetchResult
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.SerieFull
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/** ViewModel companion for the SeriesListFragment. */
class SeriesListViewModel(
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
}