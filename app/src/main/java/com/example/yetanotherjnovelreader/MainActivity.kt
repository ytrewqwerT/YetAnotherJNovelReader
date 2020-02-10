package com.example.yetanotherjnovelreader

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.example.yetanotherjnovelreader.activitypart.PartActivity
import com.example.yetanotherjnovelreader.common.*
import com.example.yetanotherjnovelreader.data.Part
import com.example.yetanotherjnovelreader.data.Repository
import com.example.yetanotherjnovelreader.data.Series
import com.example.yetanotherjnovelreader.data.Volume

private const val TAG = "MainActivity"

private const val SERIES_LIST_FRAGMENT_ID = 1
private const val VOLUMES_LIST_FRAGMENT_ID = 2
private const val PARTS_LIST_FRAGMENT_ID = 3
private const val SERIES_LIST_FRAGMENT_TAG = "SERIES_LIST_FRAGMENT"
private const val VOLUMES_LIST_FRAGMENT_TAG = "VOLUMES_LIST_FRAGMENT"
private const val PARTS_LIST_FRAGMENT_TAG = "PARTS_LIST_FRAGMENT"

class MainActivity : AppCompatActivity(), LoginResultListener {

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

        setListItemFragment(SERIES_LIST_FRAGMENT_ID, SERIES_LIST_FRAGMENT_TAG)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        appBarMenu = menu
        updateMenu()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.account_login -> {
            LoginDialog().show(supportFragmentManager, "LOGIN_DIALOG")
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun onListItemInteraction(item: ListItem) {
        val curFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)
        when (curFragment?.tag) {
            SERIES_LIST_FRAGMENT_TAG -> onSeriesListItemInteraction(item as? Series)
            VOLUMES_LIST_FRAGMENT_TAG -> onVolumesListItemInteraction(item as? Volume)
            PARTS_LIST_FRAGMENT_TAG -> onPartsListItemInteraction(item as? Part)
        }
    }

    private fun onSeriesListItemInteraction(serie: Series?) {
        Log.i(TAG, "Series clicked: ${serie?.title}")
        if (serie != null) {
            listItemViewModel.setItemList(VOLUMES_LIST_FRAGMENT_ID, emptyList())
            repository.getSerieVolumes(serie) {
                listItemViewModel.setItemList(VOLUMES_LIST_FRAGMENT_ID, it)
            }
            setListItemFragment(VOLUMES_LIST_FRAGMENT_ID, VOLUMES_LIST_FRAGMENT_TAG)
        }
    }

    private fun onVolumesListItemInteraction(volume: Volume?) {
        Log.i(TAG, "Volume clicked: ${volume?.title}")
        if (volume != null) {
            listItemViewModel.setItemList(PARTS_LIST_FRAGMENT_ID, emptyList())
            repository.getVolumeParts(volume) {
                listItemViewModel.setItemList(PARTS_LIST_FRAGMENT_ID, it)
            }
            setListItemFragment(PARTS_LIST_FRAGMENT_ID, PARTS_LIST_FRAGMENT_TAG)
        }
    }

    private fun onPartsListItemInteraction(part: Part?) {
        Log.i(TAG, "Part clicked: ${part?.title}")
        if (part != null) {
            repository.getPart(part) {
                if (it != null) {
                    val intent = Intent(this, PartActivity::class.java)
                    intent.putExtra(PartActivity.EXTRA_PART_ID, part.id)
                    startActivity(intent)
                }
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
        if (repository.getUsername() != null) {
            val nameHolder = appBarMenu?.findItem(R.id.account_name)
            nameHolder?.title = repository.getUsername()
        }
    }

    override fun onLoginResult(loggedIn: Boolean) {
        updateMenu()
    }
}