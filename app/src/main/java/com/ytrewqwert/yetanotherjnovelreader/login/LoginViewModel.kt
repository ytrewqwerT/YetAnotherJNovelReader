package com.ytrewqwert.yetanotherjnovelreader.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.SingleLiveEvent
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import kotlinx.coroutines.launch

/** Exposes data for a [LoginDialog]. */
class LoginViewModel(private val repository: Repository) : ViewModel() {
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    /** Notifies an observer of the result of a call to [login]. */
    val loginEvent = SingleLiveEvent<Boolean>()

    /** Attempts to login using the current values in [email] and [password]. */
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