package com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.serievolumeslist

import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist.SwipeableListViewModel
import com.ytrewqwert.yetanotherjnovelreader.data.FetchResult
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeFull
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/** ViewModel companion for the SerieVolumesListFragment. */
class SerieVolumesListViewModel(
    private val repository: Repository,
    private val serieId: String
) : SwipeableListViewModel<VolumeFull>(repository) {

    override val itemsSourceFlow = repository.getSerieVolumesFlow(serieId)

    private val fetchedVolumes = HashSet<String>()

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

    /** Toggles the follow status of the series whose volumes are being listed. */
    fun toggleFollow() {
        viewModelScope.launch {
            if (repository.isFollowed(serieId)) repository.unfollowSeries(serieId)
            else repository.followSeries(serieId)
        }
    }

    /**
     * Fetches the parts that belong to the volume with id [volumeId] to the repository. Only
     * performs the fetch once for each given volume id.
     */
    fun fetchVolumeParts(volumeId: String) {
        // Maayyybbbbeeeee also reset [fetchedVolumes] on refresh (?) so that freshly released parts
        // would get loaded in, though at that point, new series and volumes would also need to be
        // accounted for for consistency.
        if (!fetchedVolumes.contains(volumeId)) {
            fetchedVolumes.add(volumeId)
            viewModelScope.launch {
                repository.fetchVolumeParts(volumeId, 0, 0)
            }
        }
    }
}