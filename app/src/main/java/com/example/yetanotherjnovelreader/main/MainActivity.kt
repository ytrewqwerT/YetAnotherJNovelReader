package com.example.yetanotherjnovelreader.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.viewpager.widget.ViewPager
import com.example.yetanotherjnovelreader.R
import com.example.yetanotherjnovelreader.addOnPageSelectedListener
import com.example.yetanotherjnovelreader.common.ListItemViewModel
import com.example.yetanotherjnovelreader.data.Part
import com.example.yetanotherjnovelreader.data.Repository
import com.example.yetanotherjnovelreader.login.LoginDialog
import com.example.yetanotherjnovelreader.login.LoginResultListener
import com.example.yetanotherjnovelreader.partreader.PartActivity

class MainActivity : AppCompatActivity(),
    LoginResultListener {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val viewModel by viewModels<ListItemViewModel>()
    private val repository by lazy { Repository.getInstance(applicationContext)}
    private var appBarMenu: Menu? = null
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        viewPager = findViewById(R.id.pager)
        val viewPagerAdapter = MainPagerAdapter(supportFragmentManager)
        viewPager.adapter = viewPagerAdapter

        val recentPartsFragId = MainPagerAdapter.ChildFragments.RECENT_PARTS.ordinal
        repository.getRecentParts { viewModel.setItemList(recentPartsFragId, it) }
        viewModel.itemClickedEvent.observe(this) {
            onPartsListItemInteraction(it.item as? Part)
        }

        viewPager.addOnPageSelectedListener { position: Int ->
            with (supportFragmentManager.beginTransaction()) {
                val fragment = viewPagerAdapter.getItem(position)
                setPrimaryNavigationFragment(fragment)
                commit()
            }
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

    private fun onPartsListItemInteraction(part: Part?) {
        Log.d(TAG, "Part clicked: ${part?.title}")
        if (part != null) {
            repository.getPart(part) {
                if (it != null) {
                    val intent = Intent(this, PartActivity::class.java)
                    intent.putExtra(PartActivity.EXTRA_PART_ID, part.id)
                    startActivity(intent)
                }
            }
        } else Log.e(TAG, "Clicked item handled by MainActivity was null")
    }
}