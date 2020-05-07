package com.ytrewqwert.yetanotherjnovelreader.main

import androidx.lifecycle.*
import com.ytrewqwert.yetanotherjnovelreader.SingleLiveEvent
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {

    val logoutEvent = SingleLiveEvent<Boolean>()
    val recentParts =
        repository.getRecentParts(viewModelScope).asLiveData(viewModelScope.coroutineContext)

    private val _isFilterFollowing = MutableLiveData(repository.isFilterFollowing)
    val isFilterFollowing: LiveData<Boolean> = _isFilterFollowing

    fun logout() {
        viewModelScope.launch { logoutEvent.value = repository.logout() }
    }

    fun fetchPartProgress() {
        viewModelScope.launch { repository.fetchPartProgress() }
    }
    fun fetchRecentParts(onComplete: (success: Boolean) -> Unit = {}) {
        viewModelScope.launch { repository.getRecentParts(viewModelScope, onComplete) }
    }
    fun loggedIn() = repository.loggedIn()
    fun getUsername() = repository.getUsername()

    fun toggleFilterFollowing() {
        repository.isFilterFollowing = isFilterFollowing.value == false
        _isFilterFollowing.value = isFilterFollowing.value == false
        // TODO: Add list filtering code
    }
}