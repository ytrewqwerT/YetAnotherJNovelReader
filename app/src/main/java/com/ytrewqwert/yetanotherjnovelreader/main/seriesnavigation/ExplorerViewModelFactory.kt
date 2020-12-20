package com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ytrewqwert.yetanotherjnovelreader.data.Repository

class ExplorerViewModelFactory (private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExplorerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExplorerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}