package com.ytrewqwert.yetanotherjnovelreader.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ytrewqwert.yetanotherjnovelreader.data.Repository

class LoginViewModel(private val repository: Repository) : ViewModel() {
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    suspend fun login(): Boolean {
        val emailText = email.value
        val passwordText = password.value

        if (emailText.isNullOrEmpty() || passwordText.isNullOrEmpty()) return false
        return repository.login(emailText, passwordText)
    }
}