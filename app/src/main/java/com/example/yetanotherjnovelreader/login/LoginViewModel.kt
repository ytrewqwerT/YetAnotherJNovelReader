package com.example.yetanotherjnovelreader.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

}