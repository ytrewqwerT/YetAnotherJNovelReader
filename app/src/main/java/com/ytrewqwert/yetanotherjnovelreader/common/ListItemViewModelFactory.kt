package com.ytrewqwert.yetanotherjnovelreader.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ytrewqwert.yetanotherjnovelreader.data.Repository

class ListItemViewModelFactory(private val repository: Repository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListItemViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}