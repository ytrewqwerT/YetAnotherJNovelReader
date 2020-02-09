package com.example.yetanotherjnovelreader

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.example.yetanotherjnovelreader.common.ListItem
import com.example.yetanotherjnovelreader.common.ListItemFragment
import com.example.yetanotherjnovelreader.common.ListItemViewModel
import com.example.yetanotherjnovelreader.data.Repository
import com.example.yetanotherjnovelreader.data.Series

private const val TAG = "MainActivity"

private const val SERIES_LIST_FRAGMENT_ID = 1
private const val SERIES_FRAGMENT_ID = 2
private const val SERIES_LIST_FRAGMENT_TAG = "SERIES_LIST_FRAGMENT"
private const val SERIES_FRAGMENT_TAG = "SERIES_FRAGMENT"

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<ListItemViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.itemClickedEvent.observe(this) {
            onListItemInteraction(it.item)
        }

        val repository = Repository.getInstance(applicationContext)
        repository.getSeries {
            viewModel.setItemList(SERIES_LIST_FRAGMENT_ID, it)
        }

        with (supportFragmentManager.beginTransaction()) {
            val args = Bundle()
            args.putInt(ListItemFragment.ARG_ID, SERIES_LIST_FRAGMENT_ID)
            add(
                R.id.main_fragment_container,
                ListItemFragment::class.java,
                args,
                SERIES_LIST_FRAGMENT_TAG
            )
            commit()
        }
    }

    private fun onListItemInteraction(item: ListItem) {
        val curFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)
        when (curFragment?.tag) {
            SERIES_LIST_FRAGMENT_TAG -> onSeriesListItemInteraction(item as? Series)
        }
    }

    private fun onSeriesListItemInteraction(item: Series?) {
        Log.i(TAG, "Series clicked: ${item?.title}")
        if (item != null) {
            with (supportFragmentManager.beginTransaction()) {
                val args = Bundle()
                args.putInt(ListItemFragment.ARG_ID, SERIES_FRAGMENT_ID)
                replace(
                    R.id.main_fragment_container,
                    ListItemFragment::class.java,
                    args,
                    SERIES_FRAGMENT_TAG
                )
                addToBackStack(null)
                commit()
            }
        }
    }
}