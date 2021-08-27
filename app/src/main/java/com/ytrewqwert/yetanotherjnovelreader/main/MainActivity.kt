package com.ytrewqwert.yetanotherjnovelreader.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.viewpager.widget.ViewPager
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.addOnPageSelectedListener
import com.ytrewqwert.yetanotherjnovelreader.common.RepositoriedViewModelFactory
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.login.LoginDialog
import com.ytrewqwert.yetanotherjnovelreader.login.LoginResultListener
import com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.ExplorerFragment

/** The app's entry point. Shows the user lists of available parts for reading. */
class MainActivity : AppCompatActivity(), LoginResultListener {

    private val viewModel by viewModels<MainViewModel> {
        RepositoriedViewModelFactory(Repository.getInstance(this))
    }

    private var appBarMenu: Menu? = null
    private lateinit var viewPager: ViewPager
    private val activePagerFragment: Fragment? get() = supportFragmentManager.findFragmentByTag(
        "android:switcher:${R.id.pager}:${viewPager.currentItem}"
    )

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

        viewModel.logoutEvent.observe(this) { loggedOut ->
            // TODO: Extract string resources.
            if (loggedOut) onLoginResult(false)
            else Toast.makeText(this, "Logout failed", Toast.LENGTH_LONG).show()
        }
        viewModel.isFilterFollowing.observe(this) {
            val followMenuItem = appBarMenu?.findItem(R.id.following)
            followMenuItem?.isChecked = it
            updateMenu()
        }
        viewModel.followFailureEvent.observe(this) {
            val followResultText = when (it) {
                MainViewModel.FollowResult.FOLLOW_FAILURE -> "Failed to follow series"
                MainViewModel.FollowResult.UNFOLLOW_FAILURE -> "Failed to unfollow series"
                else -> return@observe
            }
            Toast.makeText(this, followResultText, Toast.LENGTH_LONG).show()
        }
        viewModel.changePageEvent.observe(this) {
            val explorerPageId = MainPagerAdapter.ChildFragments.EXPLORER.ordinal
            viewPager.currentItem = explorerPageId
            val explorerFragment =
                viewPagerAdapter.getItem(explorerPageId) as? ExplorerFragment
            when (it) {
                is MainViewModel.PageContent.SeriePage ->
                    explorerFragment?.onSeriesListItemInteraction(it.serieId)
                is MainViewModel.PageContent.VolumePage ->
                    explorerFragment?.onVolumesListItemInteraction(it.volumeId)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        appBarMenu = menu
        menu.findItem(R.id.following)?.isChecked = viewModel.isFilterFollowing.value ?: false
        updateMenu()
        return true
    }

    override fun onLoginResult(loggedIn: Boolean) {
        // TODO: Extract string resources.
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
            if (viewModel.isLoggedIn()) viewModel.logout()
            else LoginDialog().show(supportFragmentManager, "LOGIN_DIALOG")
            true
        }
        R.id.following -> {
            viewModel.toggleFilterFollowing()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean =
        viewModel.processContextMenuSelection(item.itemId) || super.onContextItemSelected(item)

    private fun updateMenu() {
        val nameHolder = appBarMenu?.findItem(R.id.account_name)
        val loginItem = appBarMenu?.findItem(R.id.account_login)
        if (viewModel.isLoggedIn()) {
            nameHolder?.title = viewModel.getUsername()
            loginItem?.title = getString(R.string.logout)
        } else {
            nameHolder?.title = getString(R.string.not_logged_in)
            loginItem?.title = getString(R.string.login)
        }

        val followMenuItem = appBarMenu?.findItem(R.id.following)
        followMenuItem?.icon = if (followMenuItem?.isChecked == true) {
            ResourcesCompat.getDrawable(resources, R.drawable.ic_star_24dp, null)
        } else {
            ResourcesCompat.getDrawable(resources, R.drawable.ic_star_border_24dp, null)
        }
    }
}