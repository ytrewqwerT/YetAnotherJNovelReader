package com.ytrewqwert.yetanotherjnovelreader.main

import androidx.lifecycle.ViewModel
import com.ytrewqwert.yetanotherjnovelreader.data.Part
import com.ytrewqwert.yetanotherjnovelreader.data.Repository

class MainViewModel(private val repository: Repository) : ViewModel() {
    fun logout(callback: (String) -> Unit) {
        repository.logout { loggedOut ->
            val resultText = if (loggedOut) "Logout Successful" else "Logout Failed"
            callback(resultText)
        }
    }
    fun getRecentParts(callback: (List<Part>) -> Unit) { repository.getRecentParts(callback) }
    fun loggedIn() = repository.loggedIn()
    fun getUsername() = repository.getUsername()
}