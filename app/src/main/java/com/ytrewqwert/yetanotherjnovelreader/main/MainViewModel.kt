package com.ytrewqwert.yetanotherjnovelreader.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.data.Part
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {
    fun logout(callback: (String) -> Unit) {
        repository.logout { loggedOut ->
            val resultText = if (loggedOut) "Logout Successful" else "Logout Failed"
            callback(resultText)
        }
    }
    fun getRecentParts(callback: (List<Part>) -> Unit) {
        repository.getRecentParts {
            viewModelScope.launch {
                if (loggedIn()) {
                    while (!repository.progressReady) delay(100)
                }
                callback(it)
            }
        }
    }
    fun loggedIn() = repository.loggedIn()
    fun getUsername() = repository.getUsername()
}