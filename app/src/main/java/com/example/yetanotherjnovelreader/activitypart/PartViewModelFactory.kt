package com.example.yetanotherjnovelreader.activitypart

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yetanotherjnovelreader.data.Repository

class PartViewModelFactory(
    private val repository: Repository,
    private val resources: Resources,
    private val partId: String,
    private val imgWidth: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PartViewModel(
                repository,
                resources,
                partId,
                imgWidth
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}