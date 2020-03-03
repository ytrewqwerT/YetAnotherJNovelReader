package com.ytrewqwert.yetanotherjnovelreader.main

import androidx.lifecycle.ViewModel
import com.ytrewqwert.yetanotherjnovelreader.data.Part
import com.ytrewqwert.yetanotherjnovelreader.data.Repository

class MainViewModel(private val repository: Repository) : ViewModel() {

    suspend fun logout() = repository.logout()

    suspend fun fetchPartProgress(): Boolean {
        return repository.fetchPartProgress()
    }
    suspend fun getRecentParts(): List<Part> {
        return repository.getRecentParts()
    }
    fun loggedIn() = repository.loggedIn()
    fun getUsername() = repository.getUsername()
}