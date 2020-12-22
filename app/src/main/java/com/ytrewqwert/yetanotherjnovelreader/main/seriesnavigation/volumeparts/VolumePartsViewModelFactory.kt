package com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.volumeparts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ytrewqwert.yetanotherjnovelreader.data.Repository

class VolumePartsViewModelFactory(
    private val repository: Repository,
    private val volumeId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VolumePartsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VolumePartsViewModel(repository, volumeId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}