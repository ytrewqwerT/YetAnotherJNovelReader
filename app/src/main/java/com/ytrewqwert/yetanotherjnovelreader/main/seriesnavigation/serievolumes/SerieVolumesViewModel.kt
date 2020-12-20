package com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.serievolumes

import com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist.SwipeableListViewModel
import com.ytrewqwert.yetanotherjnovelreader.data.FetchResult
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeFull

class SerieVolumesViewModel(
    private val repository: Repository,
    private val serieId: String
) : SwipeableListViewModel<VolumeFull>(repository) {

    override val itemsSourceFlow = repository.getSerieVolumesFlow(serieId)

    init {
        postInitialisationTasks()
    }

    override suspend fun performPageFetch(amount: Int, offset: Int): FetchResult? =
        repository.fetchSerieVolumes(serieId, amount, offset)
}