package com.ytrewqwert.yetanotherjnovelreader.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import kotlinx.coroutines.flow.map

class PreferenceViewModel(repository: Repository) : ViewModel() {
    val marginsDp = repository.getReaderSettingsFlow().map {
        it.marginsDp
    }.asLiveData(viewModelScope.coroutineContext)

    val pageTurnAreasPc = repository.getReaderSettingsFlow().map {
        it.pageTurnAreasPc
    }.asLiveData(viewModelScope.coroutineContext)
}