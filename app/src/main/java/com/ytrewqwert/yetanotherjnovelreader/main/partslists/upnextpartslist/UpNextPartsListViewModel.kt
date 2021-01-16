package com.ytrewqwert.yetanotherjnovelreader.main.partslists.upnextpartslist

import com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist.SwipeableListViewModel
import com.ytrewqwert.yetanotherjnovelreader.data.FetchResult
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull
import kotlinx.coroutines.flow.combine

/** ViewModel companion for the RecentPartsListFragment. */
class UpNextPartsListViewModel(
    private val repository: Repository
) : SwipeableListViewModel<PartFull>(repository) {

    override val itemsSourceFlow = repository.getUpNextPartsFlow()
        .combine(repository.isFilterFollowing) { parts, filterOn ->
            parts.filter { !filterOn || it.isFollowed() }
        }

    init {
        postInitialisationTasks()
    }

    override suspend fun performPageFetch(amount: Int, offset: Int): FetchResult =
        repository.fetchUpNextParts()
}