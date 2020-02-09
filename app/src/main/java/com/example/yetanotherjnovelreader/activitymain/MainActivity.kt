package com.example.yetanotherjnovelreader.activitymain

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.example.yetanotherjnovelreader.R
import com.example.yetanotherjnovelreader.data.RemoteRepository
import com.example.yetanotherjnovelreader.data.Series

private const val TAG = "MainActivity"
private const val SERIES_LIST_FRAGMENT_ID = 1

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<ListItemViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repository = RemoteRepository.getInstance(applicationContext)
        repository.getSeries {
            viewModel.setItemList(SERIES_LIST_FRAGMENT_ID, it)
        }
        viewModel.itemClickedEvent.observe(this) {
            onListItemInteraction(it.item as Series)
        }

        with (supportFragmentManager.beginTransaction()) {
            val args = Bundle()
            args.putInt(ListItemFragment.ARG_ID, SERIES_LIST_FRAGMENT_ID)
            add(R.id.main_fragment_container, ListItemFragment::class.java, args)
            commit()
        }
    }

    private fun onListItemInteraction(item: Series) {
        // TODO: Start new activity
        Log.i(TAG, "Item clicked: ${item.title}")
    }
}