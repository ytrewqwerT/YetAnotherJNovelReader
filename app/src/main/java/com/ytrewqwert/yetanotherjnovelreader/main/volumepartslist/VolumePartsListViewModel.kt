package com.ytrewqwert.yetanotherjnovelreader.main.volumepartslist

import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist.SwipeableListViewModel
import com.ytrewqwert.yetanotherjnovelreader.data.FetchResult
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/** ViewModel companion for the VolumePartsListFragment. */
class VolumePartsListViewModel(
    private val repository: Repository,
    private val volumeId: String
) : SwipeableListViewModel<PartFull>(repository) {

    override val itemsSourceFlow = repository.getVolumePartsFlow(volumeId).map { parts ->
        // Don't show the volume title in the volume parts list.
        if (repository.getVolumes(volumeId).isEmpty()) repository.fetchVolume(volumeId)
        val volumeTitle = repository.getVolumes(volumeId).firstOrNull()?.volume?.title ?: ""
        parts.map { part ->
            val partTitle = part.part.title
            val trimmedTitle = partTitle.removePrefix(volumeTitle).trim()
            part.copy(
                part = part.part.copy(title = trimmedTitle)
            )
        }
    }

    init {
        postInitialisationTasks()
        viewModelScope.launch {
            repository.getVolumeFlow(volumeId).collect {
                setHeader(it)
            }
        }
    }

    override suspend fun performPageFetch(amount: Int, offset: Int): FetchResult? =
        repository.fetchVolumeParts(volumeId, 0, 0)
}