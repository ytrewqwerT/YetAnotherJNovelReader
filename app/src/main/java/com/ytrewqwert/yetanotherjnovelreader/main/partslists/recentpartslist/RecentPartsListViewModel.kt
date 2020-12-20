package com.ytrewqwert.yetanotherjnovelreader.main.partslists.recentpartslist

import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist.SwipeableListViewModel
import com.ytrewqwert.yetanotherjnovelreader.data.FetchResult
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class RecentPartsListViewModel(
    private val repository: Repository
) : SwipeableListViewModel<PartFull>(repository) {
    override val itemsSourceFlow = repository.getRecentPartsFlow()
        .combine(repository.isFilterFollowing) { parts, filterOn ->
            parts.filter { !filterOn || it.isFollowed() }
        }

    private var isFilterFollowing = false

    init {
        viewModelScope.launch {
            repository.isFilterFollowing.collect { isFilterFollowing = it }
        }
        collectListData()
    }

    fun toggleFollow(part: PartFull) {
        val following: Boolean = part.isFollowed()
        val serieId = part.part.serieId
        viewModelScope.launch {
            if (following) repository.unfollowSeries(serieId)
            else repository.followSeries(serieId)
        }
    }

    override suspend fun performPageFetch(amount: Int, offset: Int): FetchResult? =
        repository.fetchRecentParts(amount, offset, isFilterFollowing)
}