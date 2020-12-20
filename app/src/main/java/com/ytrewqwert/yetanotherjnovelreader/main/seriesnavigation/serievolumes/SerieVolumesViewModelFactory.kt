package com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.serievolumes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ytrewqwert.yetanotherjnovelreader.data.Repository

class SerieVolumesViewModelFactory(
    private val repository: Repository,
    private val serieId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SerieVolumesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SerieVolumesViewModel(repository, serieId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}