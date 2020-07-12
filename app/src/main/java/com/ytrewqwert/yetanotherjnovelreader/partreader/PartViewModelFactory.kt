package com.ytrewqwert.yetanotherjnovelreader.partreader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ytrewqwert.yetanotherjnovelreader.data.Repository

class PartViewModelFactory(
    private val repository: Repository,
    private val partId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PartViewModel(repository, partId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}