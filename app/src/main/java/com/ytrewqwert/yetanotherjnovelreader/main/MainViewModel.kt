package com.ytrewqwert.yetanotherjnovelreader.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.SingleLiveEvent
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {
    val logoutEvent = SingleLiveEvent<Boolean>()

    @ExperimentalCoroutinesApi
    val recentParts = repository.getRecentParts(viewModelScope)
        .combine(repository.isFilterFollowing) { parts, filterOn ->
            parts.filter { !filterOn || it.isFollowed() }
        }.asLiveData(viewModelScope.coroutineContext)

    @ExperimentalCoroutinesApi
    val isFilterFollowing =
        repository.isFilterFollowing.asLiveData(viewModelScope.coroutineContext)

    fun logout() {
        viewModelScope.launch { logoutEvent.value = repository.logout() }
    }

    fun fetchRecentParts(onComplete: (success: Boolean) -> Unit = {}) {
        viewModelScope.launch { repository.getRecentParts(viewModelScope, onComplete) }
    }
    fun loggedIn() = repository.loggedIn()
    fun getUsername() = repository.getUsername()

    @ExperimentalCoroutinesApi
    fun toggleFilterFollowing() {
        repository.setIsFilterFollowing(isFilterFollowing.value == false)
    }
}