package com.ytrewqwert.yetanotherjnovelreader.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.SingleLiveEvent
import com.ytrewqwert.yetanotherjnovelreader.data.Part
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {

    val logoutEvent = SingleLiveEvent<Boolean>()
    private val _recentParts = MutableLiveData<List<Part>>()
    val recentParts: LiveData<List<Part>> = _recentParts

    fun logout() {
        viewModelScope.launch { logoutEvent.value = repository.logout() }
    }

    fun fetchPartProgress() {
        viewModelScope.launch { repository.fetchPartProgress() }
    }
    fun fetchRecentParts() {
        viewModelScope.launch { _recentParts.value = repository.getRecentParts() }
    }
    fun loggedIn() = repository.loggedIn()
    fun getUsername() = repository.getUsername()
}