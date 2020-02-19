package com.ytrewqwert.yetanotherjnovelreader.main

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
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.addOnPageSelectedListener
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemViewModel
import com.ytrewqwert.yetanotherjnovelreader.data.Part
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.login.LoginDialog
import com.ytrewqwert.yetanotherjnovelreader.login.LoginResultListener
import com.ytrewqwert.yetanotherjnovelreader.partreader.PartActivity

class MainActivity : AppCompatActivity(),
    LoginResultListener {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val mainViewModel by viewModels<MainViewModel>()
    private val recentsListViewModel by viewModels<ListItemViewModel>()
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
        // Set primary navigation fragment to the focused viewpager page to allow interception
        viewPager.addOnPageSelectedListener { position: Int ->
            with (supportFragmentManager.beginTransaction()) {
                val fragment = viewPagerAdapter.getItem(position)
                setPrimaryNavigationFragment(fragment)
                commit()
            }
        }

        // Catch Part clicks from the recent parts page in the viewpager
        val recentPartsFragId = MainPagerAdapter.ChildFragments.RECENT_PARTS.ordinal
        mainViewModel.getRecentParts { recentsListViewModel.setItemList(recentPartsFragId, it) }
        recentsListViewModel.itemClickedEvent.observe(this) {
            onPartsListItemInteraction(it.item as? Part)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        appBarMenu = menu
        updateMenu()
        return true
    }

    override fun onLoginResult(loggedIn: Boolean) {
        if (loggedIn) Toast.makeText(this, "Logged in", Toast.LENGTH_LONG).show()
        updateMenu()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.account_login -> {
            if (mainViewModel.loggedIn()) {
                mainViewModel.logout { logoutResult ->
                    Toast.makeText(this, logoutResult, Toast.LENGTH_LONG).show()
                    updateMenu()
                }
            } else {
                LoginDialog().show(supportFragmentManager, "LOGIN_DIALOG")
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun onPartsListItemInteraction(part: Part?) {
        Log.d(TAG, "Part clicked: ${part?.title}")
        if (part != null) {
            val intent = Intent(this, PartActivity::class.java)
            intent.putExtra(PartActivity.EXTRA_PART_ID, part.id)
            startActivity(intent)
        } else Log.e(TAG, "Clicked item handled by MainActivity was null")
    }

    private fun updateMenu() {
        val nameHolder = appBarMenu?.findItem(R.id.account_name)
        val loginItem = appBarMenu?.findItem(R.id.account_login)
        if (mainViewModel.loggedIn()) {
            nameHolder?.title = mainViewModel.getUsername()
            loginItem?.title = getString(R.string.logout)
        } else {
            nameHolder?.title = getString(R.string.not_logged_in)
            loginItem?.title = getString(R.string.login)
        }
    }
}