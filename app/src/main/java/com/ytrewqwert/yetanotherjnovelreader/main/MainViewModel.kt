package com.ytrewqwert.yetanotherjnovelreader.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.SingleLiveEvent
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {

    val logoutEvent = SingleLiveEvent<Boolean>()
    val recentParts =
        repository.getRecentParts(viewModelScope).asLiveData(viewModelScope.coroutineContext)

    fun logout() {
        viewModelScope.launch { logoutEvent.value = repository.logout() }
    }

    fun fetchPartProgress() {
        viewModelScope.launch { repository.fetchPartProgress() }
    }
    fun fetchRecentParts() {
        viewModelScope.launch { repository.getRecentParts(viewModelScope) }
    }
    fun loggedIn() = repository.loggedIn()
    fun getUsername() = repository.getUsername()
}