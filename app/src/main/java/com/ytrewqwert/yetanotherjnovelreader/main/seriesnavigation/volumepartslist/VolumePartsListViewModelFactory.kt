package com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.volumepartslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ytrewqwert.yetanotherjnovelreader.data.Repository

/** ViewModelProvider.Factory for creating a VolumePartsListViewModel. */
class VolumePartsListViewModelFactory(
    private val repository: Repository,
    private val volumeId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VolumePartsListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VolumePartsListViewModel(repository, volumeId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}