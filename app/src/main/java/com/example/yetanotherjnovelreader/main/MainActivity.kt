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
import com.example.yetanotherjnovelreader.R
import com.example.yetanotherjnovelreader.common.ListItem
import com.example.yetanotherjnovelreader.common.ListItemFragment
import com.example.yetanotherjnovelreader.common.ListItemViewModel
import com.example.yetanotherjnovelreader.data.Part
import com.example.yetanotherjnovelreader.data.Repository
import com.example.yetanotherjnovelreader.data.Series
import com.example.yetanotherjnovelreader.data.Volume
import com.example.yetanotherjnovelreader.login.LoginDialog
import com.example.yetanotherjnovelreader.login.LoginResultListener
import com.example.yetanotherjnovelreader.partreader.PartActivity

class MainActivity : AppCompatActivity(),
    LoginResultListener {

    companion object {
        private const val TAG = "MainActivity"

        private const val SERIES_LIST_FRAGMENT_ID = 1
        private const val VOLUMES_LIST_FRAGMENT_ID = 2
        private const val PARTS_LIST_FRAGMENT_ID = 3
        private const val SERIES_LIST_FRAGMENT_TAG = "SERIES_LIST_FRAGMENT"
        private const val VOLUMES_LIST_FRAGMENT_TAG = "VOLUMES_LIST_FRAGMENT"
        private const val PARTS_LIST_FRAGMENT_TAG = "PARTS_LIST_FRAGMENT"
    }

    private val listItemViewModel by viewModels<ListItemViewModel>()
    private val repository by lazy { Repository.getInstance(applicationContext)}
    private var appBarMenu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        listItemViewModel.itemClickedEvent.observe(this) {
            onListItemInteraction(it.item)
        }

        repository.getSeries {
            listItemViewModel.setItemList(SERIES_LIST_FRAGMENT_ID, it)
        }

        setListItemFragment(
            SERIES_LIST_FRAGMENT_ID,
            SERIES_LIST_FRAGMENT_TAG
        )
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

    private fun onListItemInteraction(item: ListItem) {
        when (item) {
            is Series -> onSeriesListItemInteraction(item)
            is Volume -> onVolumesListItemInteraction(item)
            is Part -> onPartsListItemInteraction(item)
            else -> Log.e(TAG, "ListItemInteraction for $item's type not handled")
        }
    }

    private fun onSeriesListItemInteraction(serie: Series) {
        Log.d(TAG, "Series clicked: ${serie.title}")
        listItemViewModel.setItemList(VOLUMES_LIST_FRAGMENT_ID, emptyList())
        repository.getSerieVolumes(serie) {
            listItemViewModel.setItemList(VOLUMES_LIST_FRAGMENT_ID, it)
        }
        setListItemFragment(
            VOLUMES_LIST_FRAGMENT_ID,
            VOLUMES_LIST_FRAGMENT_TAG
        )
    }

    private fun onVolumesListItemInteraction(volume: Volume) {
        Log.d(TAG, "Volume clicked: ${volume.title}")
        listItemViewModel.setItemList(PARTS_LIST_FRAGMENT_ID, emptyList())
        repository.getVolumeParts(volume) {
            listItemViewModel.setItemList(PARTS_LIST_FRAGMENT_ID, it)
        }
        setListItemFragment(
            PARTS_LIST_FRAGMENT_ID,
            PARTS_LIST_FRAGMENT_TAG
        )
    }

    private fun onPartsListItemInteraction(part: Part) {
        Log.d(TAG, "Part clicked: ${part.title}")
        repository.getPart(part) {
            if (it != null) {
                val intent = Intent(this, PartActivity::class.java)
                intent.putExtra(PartActivity.EXTRA_PART_ID, part.id)
                startActivity(intent)
            }
        }
    }

    private fun setListItemFragment(fragmentId: Int, fragmentTag: String?) {
        with (supportFragmentManager.beginTransaction()) {
            val args = Bundle()
            args.putInt(ListItemFragment.ARG_ID, fragmentId)
            replace(
                R.id.main_fragment_container,
                ListItemFragment::class.java,
                args,
                fragmentTag
            )
            if (supportFragmentManager.findFragmentById(R.id.main_fragment_container) != null) {
                addToBackStack(null)
            }
            commit()
        }
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