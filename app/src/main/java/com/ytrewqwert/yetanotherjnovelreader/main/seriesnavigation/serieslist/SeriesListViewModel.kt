package com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.serieslist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
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
    val searchQuery = MutableLiveData("")

    override val itemsSourceFlow = repository.getSeriesFlow()
        .combine(repository.isFilterFollowing) { series, filterOn ->
            // Can optimise (assuming compiler doesn't do so already) by pulling the filterOn
            // condition out of the filter operation, returning the original list if filterOn is
            // false. Similarly for the searchQuery below.
            series.filter { !filterOn || it.isFollowed() }
        }.combine(searchQuery.asFlow()) { series, searchFilter ->
            series.filter { searchFilter.isEmpty() || it.serie.title.contains(searchFilter, true) }
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