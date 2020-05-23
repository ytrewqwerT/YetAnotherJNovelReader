package com.ytrewqwert.yetanotherjnovelreader.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.SingleLiveEvent
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemViewModel
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {
    val logoutEvent = SingleLiveEvent<Boolean>()

    @ExperimentalCoroutinesApi
    val isFilterFollowing =
        repository.isFilterFollowing.asLiveData(viewModelScope.coroutineContext)

    fun logout() {
        viewModelScope.launch { logoutEvent.value = repository.logout() }
    }

    @ExperimentalCoroutinesApi
    fun getRecentPartsSource(): ListItemViewModel.ListItemSource {
        val filteredPartsFlow = repository.getRecentPartsFlow()
            .combine(repository.isFilterFollowing) { series, filterOn ->
                series.filter { !filterOn || it.isFollowed() }
            }
        return ListItemViewModel.ListItemSource(
            filteredPartsFlow
        ) { _, amount, offset ->
            repository.fetchRecentParts(amount, offset)
        }
    }

    fun loggedIn() = repository.loggedIn()
    fun getUsername() = repository.getUsername()

    @ExperimentalCoroutinesApi
    fun toggleFilterFollowing() {
        repository.setIsFilterFollowing(isFilterFollowing.value == false)
    }
}