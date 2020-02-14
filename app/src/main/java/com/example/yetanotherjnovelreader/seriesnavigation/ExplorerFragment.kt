package com.example.yetanotherjnovelreader.seriesnavigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.example.yetanotherjnovelreader.R
import com.example.yetanotherjnovelreader.common.ListItem
import com.example.yetanotherjnovelreader.common.ListItemViewModel
import com.example.yetanotherjnovelreader.data.Part
import com.example.yetanotherjnovelreader.data.Repository
import com.example.yetanotherjnovelreader.data.Series
import com.example.yetanotherjnovelreader.data.Volume
import com.example.yetanotherjnovelreader.partreader.PartActivity

class ExplorerFragment : Fragment() {

    companion object {
        private const val TAG = "NavigationFragment"
        private const val SERIES_LIST_FRAGMENT_ID = 1
        private const val VOLUMES_LIST_FRAGMENT_ID = 2
        private const val PARTS_LIST_FRAGMENT_ID = 3
        private const val SERIES_LIST_FRAGMENT_TAG = "SERIES_LIST_FRAGMENT"
        private const val VOLUMES_LIST_FRAGMENT_TAG = "VOLUMES_LIST_FRAGMENT"
        private const val PARTS_LIST_FRAGMENT_TAG = "PARTS_LIST_FRAGMENT"
    }

    private val viewModel by viewModels<ListItemViewModel>()
    private val repository by lazy { Repository.getInstance(requireContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.itemClickedEvent.observe(this) { onListItemInteraction(it.item) }
        repository.getSeries { viewModel.setItemList(SERIES_LIST_FRAGMENT_ID, it) }
        setListItemFragment(SERIES_LIST_FRAGMENT_ID, SERIES_LIST_FRAGMENT_TAG)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_explorer, container, false)
    }

    private fun setListItemFragment(fragmentId: Int, fragmentTag: String?) {
        with (childFragmentManager.beginTransaction()) {
            val args = Bundle()
            args.putInt(com.example.yetanotherjnovelreader.common.ListItemFragment.ARG_ID, fragmentId)
            setCustomAnimations(
                R.animator.slide_from_right, R.animator.slide_to_left,
                R.animator.slide_from_left, R.animator.slide_to_right
            )
            replace(
                R.id.fragment_container,
                com.example.yetanotherjnovelreader.common.ListItemFragment::class.java,
                args,
                fragmentTag
            )
            if (childFragmentManager.findFragmentById(R.id.fragment_container) != null) {
                Log.d(TAG, "adding to back stack")
                addToBackStack(null)
            }
            commit()
        }
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
        viewModel.setItemList(VOLUMES_LIST_FRAGMENT_ID, emptyList())
        repository.getSerieVolumes(serie) {
            viewModel.setItemList(VOLUMES_LIST_FRAGMENT_ID, it)
        }
        setListItemFragment(
            VOLUMES_LIST_FRAGMENT_ID,
            VOLUMES_LIST_FRAGMENT_TAG
        )
    }

    private fun onVolumesListItemInteraction(volume: Volume) {
        Log.d(TAG, "Volume clicked: ${volume.title}")
        viewModel.setItemList(PARTS_LIST_FRAGMENT_ID, emptyList())
        repository.getVolumeParts(volume) {
            viewModel.setItemList(PARTS_LIST_FRAGMENT_ID, it)
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
                val intent = Intent(context, PartActivity::class.java)
                intent.putExtra(PartActivity.EXTRA_PART_ID, part.id)
                startActivity(intent)
            }
        }
    }
}
