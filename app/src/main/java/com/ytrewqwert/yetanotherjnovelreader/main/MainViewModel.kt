package com.ytrewqwert.yetanotherjnovelreader.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.SingleLiveEvent
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemViewModel
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {
    val logoutEvent = SingleLiveEvent<Boolean>()

    @ExperimentalCoroutinesApi
    val isFilterFollowing =
        repository.isFilterFollowing.asLiveData(viewModelScope.coroutineContext)

    fun logout() { viewModelScope.launch { logoutEvent.value = repository.logout() } }

    @ExperimentalCoroutinesApi
    fun getRecentPartsSource(): ListItemViewModel.ListItemSource {
        return ListItemViewModel.ListItemSource(
            repository.getRecentPartsFlow()
        ) { amount, offset, followedOnly ->
            if (followedOnly) repository.fetchRecentParts(amount, offset, true)
            else repository.fetchRecentParts(amount, offset, false)
        }
    }

    @ExperimentalCoroutinesApi
    fun getUpNextPartsSource(): ListItemViewModel.ListItemSource {
        return ListItemViewModel.ListItemSource(
            repository.getUpNextPartsFlow()
        ) { _, _, _ ->
            repository.fetchUpNextParts()
        }
    }

    fun loggedIn() = repository.loggedIn()
    fun getUsername() = repository.getUsername()

    @ExperimentalCoroutinesApi
    fun toggleFilterFollowing() {
        repository.setIsFilterFollowing(isFilterFollowing.value == false)
    }
}