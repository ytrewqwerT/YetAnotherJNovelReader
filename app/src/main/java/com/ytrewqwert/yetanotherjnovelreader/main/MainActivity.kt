package com.ytrewqwert.yetanotherjnovelreader.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.observe
import androidx.viewpager.widget.ViewPager
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.addOnPageSelectedListener
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemViewModel
import com.ytrewqwert.yetanotherjnovelreader.common.RepositoriedViewModelFactory
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull
import com.ytrewqwert.yetanotherjnovelreader.login.LoginDialog
import com.ytrewqwert.yetanotherjnovelreader.login.LoginResultListener
import com.ytrewqwert.yetanotherjnovelreader.partreader.PartActivity

/** The app's entry point. Shows the user lists of available parts for reading. */
class MainActivity : AppCompatActivity(), LoginResultListener {
    companion object {
        private const val TAG = "MainActivity"
    }

    private val mainViewModel by viewModels<MainViewModel> {
        RepositoriedViewModelFactory(Repository.getInstance(this))
    }
    private val listItemViewModel by viewModels<ListItemViewModel> {
        RepositoriedViewModelFactory(Repository.getInstance(this))
    }

    private var appBarMenu: Menu? = null
    private lateinit var viewPager: ViewPager
    private val activePagerFragment: Fragment? get() = supportFragmentManager.findFragmentByTag(
        "android:switcher:${R.id.pager}:${viewPager.currentItem}"
    )

    private val recentPartsFragId = MainPagerAdapter.ChildFragments.RECENT_PARTS.ordinal
    private val upNextPartsFragId = MainPagerAdapter.ChildFragments.UP_NEXT_PARTS.ordinal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        viewPager = findViewById(R.id.pager)
        val viewPagerAdapter = MainPagerAdapter(supportFragmentManager)
        viewPager.adapter = viewPagerAdapter
        // Set primary navigation fragment to the focused viewpager page to allow touch interception
        viewPager.addOnPageSelectedListener {
            supportFragmentManager.commit {
                setPrimaryNavigationFragment(activePagerFragment)
            }
        }

        mainViewModel.logoutEvent.observe(this) { loggedOut ->
            if (loggedOut) onLoginResult(false)
            else Toast.makeText(this, "Logout failed", Toast.LENGTH_LONG).show()
        }
        mainViewModel.isFilterFollowing.observe(this) {
            val followMenuItem = appBarMenu?.findItem(R.id.following)
            followMenuItem?.isChecked = it
            updateMenu()
        }
        listItemViewModel.itemClickedEvent.observe(this) {
            onPartsListItemInteraction(it.item as? PartFull)
        }

        listItemViewModel.setSource(recentPartsFragId, mainViewModel.getRecentPartsSource())
        listItemViewModel.setSource(upNextPartsFragId, mainViewModel.getUpNextPartsSource())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        appBarMenu = menu
        menu.findItem(R.id.following)?.isChecked = mainViewModel.isFilterFollowing.value ?: false
        updateMenu()
        return true
    }

    override fun onLoginResult(loggedIn: Boolean) {
        val loginStatusText = if (loggedIn) "Logged in" else "Logged out"
        Toast.makeText(this, loginStatusText, Toast.LENGTH_LONG).show()
        updateMenu()

        // Resuming ListItemFragments force-updates them into (un)greying out non-viewable parts
        //  and ExplorerFragment propagates the onResume to its children to do the same.
        // Probably shouldn't be using lifecycle functions like this, but ¯\_(ツ)_/¯
        activePagerFragment?.onResume()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.account_login -> {
            if (mainViewModel.isLoggedIn()) mainViewModel.logout()
            else LoginDialog().show(supportFragmentManager, "LOGIN_DIALOG")
            true
        }
        R.id.following -> {
            mainViewModel.toggleFilterFollowing()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun onPartsListItemInteraction(part: PartFull?) {
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
        if (mainViewModel.isLoggedIn()) {
            nameHolder?.title = mainViewModel.getUsername()
            loginItem?.title = getString(R.string.logout)
        } else {
            nameHolder?.title = getString(R.string.not_logged_in)
            loginItem?.title = getString(R.string.login)
        }

        val followMenuItem = appBarMenu?.findItem(R.id.following)
        followMenuItem?.icon = if (followMenuItem?.isChecked == true) {
            resources.getDrawable(R.drawable.ic_star_24dp, null)
        } else {
            resources.getDrawable(R.drawable.ic_star_border_24dp, null)
        }
    }
}