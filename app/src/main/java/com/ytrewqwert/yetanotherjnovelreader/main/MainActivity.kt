package com.ytrewqwert.yetanotherjnovelreader.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.viewpager.widget.ViewPager
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.addOnPageSelectedListener
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemViewModel
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemViewModelFactory
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.PartWithProgress
import com.ytrewqwert.yetanotherjnovelreader.login.LoginDialog
import com.ytrewqwert.yetanotherjnovelreader.login.LoginResultListener
import com.ytrewqwert.yetanotherjnovelreader.partreader.PartActivity
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(),
    LoginResultListener {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val mainViewModel by viewModels<MainViewModel> {
        MainViewModelFactory(Repository.getInstance(this))
    }
    private val recentsListViewModel by viewModels<ListItemViewModel> {
        ListItemViewModelFactory(Repository.getInstance(this))
    }

    private var appBarMenu: Menu? = null
    private lateinit var viewPager: ViewPager

    private val recentPartsFragId = MainPagerAdapter.ChildFragments.RECENT_PARTS.ordinal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        viewPager = findViewById(R.id.pager)
        val viewPagerAdapter = MainPagerAdapter(supportFragmentManager)
        viewPager.adapter = viewPagerAdapter
        // Set primary navigation fragment to the focused viewpager page to allow interception
        viewPager.addOnPageSelectedListener {
            with (supportFragmentManager.beginTransaction()) {
                val fragment = supportFragmentManager.findFragmentByTag(
                    "android:switcher:${R.id.pager}:${viewPager.currentItem}"
                )
                setPrimaryNavigationFragment(fragment)
                commit()
            }
        }

        lifecycleScope.launch {
            mainViewModel.fetchPartProgress()
        }

        observeViewModels()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        appBarMenu = menu
        updateMenu()
        return true
    }

    override fun onLoginResult(loggedIn: Boolean) {
        val loginStatusText = if (loggedIn) "Logged in" else "Logged out"
        Toast.makeText(this, loginStatusText, Toast.LENGTH_LONG).show()
        updateMenu()

        val fragment = supportFragmentManager.findFragmentByTag(
            "android:switcher:${R.id.pager}:${viewPager.currentItem}"
        )

        // Resuming ListItemFragments force-updates them into (un)greying out non-viewable parts
        //  and ExplorerFragment propagates the onResume ot its children to do the same.
        // Not a great solution, I know.
        fragment?.onResume()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.account_login -> {
            if (mainViewModel.loggedIn()) {
                mainViewModel.logout()
            } else {
                LoginDialog().show(supportFragmentManager, "LOGIN_DIALOG")
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun observeViewModels() {
        mainViewModel.logoutEvent.observe(this) { loggedOut ->
            if (loggedOut) onLoginResult(false)
            else {
                Toast.makeText(this@MainActivity, "Logout failed", Toast.LENGTH_LONG)
                    .show()
            }
        }
        mainViewModel.recentParts.observe(this) {
            recentsListViewModel.setItemList(recentPartsFragId, it)
        }

        recentsListViewModel.itemClickedEvent.observe(this) {
            onPartsListItemInteraction(it.item as? PartWithProgress)
        }
        recentsListViewModel.getItemList(recentPartsFragId).observe(this) {
            if (it != null) return@observe
            lifecycleScope.launch { mainViewModel.fetchRecentParts() }
        }
    }

    private fun onPartsListItemInteraction(part: PartWithProgress?) {
        Log.d(TAG, "Part clicked: ${part?.part?.title}")
        if (part != null) {
            val intent = Intent(this, PartActivity::class.java)
            intent.putExtra(PartActivity.EXTRA_PART_ID, part.part.id)
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