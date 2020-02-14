package com.example.yetanotherjnovelreader.main

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yetanotherjnovelreader.R
import com.example.yetanotherjnovelreader.data.Repository
import com.example.yetanotherjnovelreader.login.LoginDialog
import com.example.yetanotherjnovelreader.login.LoginResultListener
import com.example.yetanotherjnovelreader.seriesnavigation.ExplorerFragment

class MainActivity : AppCompatActivity(),
    LoginResultListener {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val repository by lazy { Repository.getInstance(applicationContext)}
    private var appBarMenu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        with (supportFragmentManager.beginTransaction()) {
            val navFragment = ExplorerFragment()
            add(R.id.main_fragment_container, navFragment)
            setPrimaryNavigationFragment(navFragment)
            commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        appBarMenu = menu
        updateMenu()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.account_login -> {
            if (repository.loggedIn()) {
                repository.logout { logoutSuccessful ->
                    if (logoutSuccessful) {
                        Toast.makeText(this, "Logout Successful", Toast.LENGTH_LONG).show()
                        updateMenu()
                    } else {
                        Toast.makeText(this, "Logout Failed", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                LoginDialog()
                    .show(supportFragmentManager, "LOGIN_DIALOG")
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun updateMenu() {
        val nameHolder = appBarMenu?.findItem(R.id.account_name)
        val loginItem = appBarMenu?.findItem(R.id.account_login)
        if (repository.loggedIn()) {
            nameHolder?.title = repository.getUsername()
            loginItem?.title = getString(R.string.logout)
        } else {
            nameHolder?.title = getString(R.string.not_logged_in)
            loginItem?.title = getString(R.string.login)
        }
    }

    override fun onLoginResult(loggedIn: Boolean) {
        Log.d(TAG, "Updating account menu")
        if (loggedIn) Toast.makeText(this, "Logged in", Toast.LENGTH_LONG).show()
        updateMenu()
    }
}