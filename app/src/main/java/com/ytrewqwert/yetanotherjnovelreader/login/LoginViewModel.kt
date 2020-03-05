package com.ytrewqwert.yetanotherjnovelreader.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.SingleLiveEvent
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: Repository) : ViewModel() {
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    val loginEvent = SingleLiveEvent<Boolean>()

    fun login() {
        val emailText = email.value
        val passwordText = password.value

        if (emailText.isNullOrEmpty() || passwordText.isNullOrEmpty()) {
            loginEvent.value = false
        } else {
            viewModelScope.launch {
                val loggedIn = repository.login(emailText, passwordText)
                loginEvent.value = loggedIn
            }
        }
    }
}