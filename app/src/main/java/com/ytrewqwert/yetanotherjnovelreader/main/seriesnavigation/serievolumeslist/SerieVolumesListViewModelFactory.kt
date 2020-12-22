package com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.serievolumeslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ytrewqwert.yetanotherjnovelreader.data.Repository

/** ViewModelProvider.Factory for creating a SerieVolumesListViewModel. */
class SerieVolumesListViewModelFactory(
    private val repository: Repository,
    private val serieId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SerieVolumesListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SerieVolumesListViewModel(repository, serieId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}