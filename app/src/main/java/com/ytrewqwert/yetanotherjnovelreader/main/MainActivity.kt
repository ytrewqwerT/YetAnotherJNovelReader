package com.ytrewqwert.yetanotherjnovelreader.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.common.RepositoriedViewModelFactory
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.login.LoginDialog
import com.ytrewqwert.yetanotherjnovelreader.login.LoginResultListener
import com.ytrewqwert.yetanotherjnovelreader.main.serievolumeslist.SerieVolumesListFragment
import com.ytrewqwert.yetanotherjnovelreader.main.volumepartslist.VolumePartsListFragment

/** The app's entry point. Shows the user lists of available parts for reading. */
class MainActivity : AppCompatActivity(), LoginResultListener {

    private val viewModel by viewModels<MainViewModel> {
        RepositoriedViewModelFactory(Repository.getInstance(this))
    }

    private var appBarMenu: Menu? = null

    private val childFragment: Fragment? get() =
        supportFragmentManager.findFragmentById(R.id.fragment_container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        setChildFragment(LandingFragment::class.java)

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
            when (it) {
                is MainViewModel.PageContent.SeriePage -> setChildFragmentToSerie(it.serieId)
                is MainViewModel.PageContent.VolumePage -> setChildFragmentToVolume(it.volumeId)
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
        childFragment?.onResume()
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

    fun setChildFragmentToSerie(serieId: String) {
        setChildFragment(
            SerieVolumesListFragment::class.java,
            bundleOf(SerieVolumesListFragment.ARG_SERIE_ID to serieId)
        )
    }

    fun setChildFragmentToVolume(volumeId: String) {
        setChildFragment(
            VolumePartsListFragment::class.java,
            bundleOf(VolumePartsListFragment.ARG_VOLUME_ID to volumeId)
        )
    }

    private fun setChildFragment(fragmentClass: Class<out Fragment>, args: Bundle? = null) {
        supportFragmentManager.commit {
            setCustomAnimations(
                R.animator.slide_from_right, R.animator.slide_to_left,
                R.animator.slide_from_left, R.animator.slide_to_right
            )
            replace(R.id.fragment_container, fragmentClass, args)
            if (supportFragmentManager.findFragmentById(R.id.fragment_container) != null) {
                addToBackStack(null)
            }
        }
    }
}