package com.ytrewqwert.yetanotherjnovelreader.main

import androidx.lifecycle.ViewModel
import com.ytrewqwert.yetanotherjnovelreader.data.Part
import com.ytrewqwert.yetanotherjnovelreader.data.Repository

class MainViewModel(private val repository: Repository) : ViewModel() {
    fun logout(callback: (Boolean) -> Unit) {
        repository.logout { loggedOut ->
            callback(loggedOut)
        }
    }
    fun fetchPartProgress(onComplete: (Boolean) -> Unit) {
        repository.fetchPartProgress { onComplete(it) }
    }
    fun getRecentParts(callback: (List<Part>) -> Unit) {
        repository.getRecentParts { callback(it) }
    }
    fun loggedIn() = repository.loggedIn()
    fun getUsername() = repository.getUsername()
}