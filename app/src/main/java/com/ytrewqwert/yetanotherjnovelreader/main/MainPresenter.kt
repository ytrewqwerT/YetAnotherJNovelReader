package com.ytrewqwert.yetanotherjnovelreader.main

import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.PartFull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class MainPresenter(scope: CoroutineScope, repository: Repository) {
    val filteredRecentParts: Flow<List<PartFull>>

    init {
        val isFilterFollow = repository.isFilterFollowing
        val followedSeries = repository.getFollowedSeries()
        val followedFilter =
            isFilterFollow.combine(followedSeries) { isFiltering, following ->
                SeriesFilter(isFiltering, following.map { it.serieId })
            }

        val recentPartsFlow = repository.getRecentParts(scope)
        filteredRecentParts =
            recentPartsFlow.combine(followedFilter) { parts, filter ->
                if (filter.enable) parts.filter { filter.serieIds.contains(it.part.serieId) }
                else parts
            }
    }

    private data class SeriesFilter(
        val enable: Boolean,
        val serieIds: List<String>
    )
}