package com.ytrewqwert.yetanotherjnovelreader.login


/** Interface for receiving the login result from a [LoginDialog]. */
interface LoginResultListener {
    /** Called upon the completion of a [LoginDialog]. */
    fun onLoginResult(loggedIn: Boolean)
}