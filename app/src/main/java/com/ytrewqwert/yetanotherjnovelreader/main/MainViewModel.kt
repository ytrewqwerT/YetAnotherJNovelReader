package com.ytrewqwert.yetanotherjnovelreader.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.SingleLiveEvent
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {
    private val presenter = MainPresenter(viewModelScope, repository)
    val logoutEvent = SingleLiveEvent<Boolean>()
    val recentParts =
        presenter.filteredRecentParts.asLiveData(viewModelScope.coroutineContext)
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

    fun toggleFilterFollowing() {
        repository.setIsFilterFollowing(isFilterFollowing.value == false)
        // TODO: Add list filtering code
    }
}