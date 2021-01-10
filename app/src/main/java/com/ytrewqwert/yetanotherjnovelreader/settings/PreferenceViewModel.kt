package com.ytrewqwert.yetanotherjnovelreader.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import kotlinx.coroutines.flow.map

class PreferenceViewModel(repository: Repository) : ViewModel() {
    val margins = repository.getReaderSettingsFlow().map {
        it.margin
    }.asLiveData(viewModelScope.coroutineContext)
}