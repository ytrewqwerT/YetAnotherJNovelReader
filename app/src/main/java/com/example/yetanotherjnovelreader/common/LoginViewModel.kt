package com.example.yetanotherjnovelreader.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yetanotherjnovelreader.data.Repository

private const val TAG = "LoginViewModel"

class LoginViewModel(private val repository: Repository) : ViewModel() {
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    fun login(callback: (Boolean) -> Unit) {
        val emailText = email.value
        val passwordText = password.value
        if (emailText.isNullOrEmpty() || passwordText.isNullOrEmpty()) {
            callback(false)
        } else {
            repository.login(emailText, passwordText, callback)
        }
    }

    class LoginViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}