package com.ytrewqwert.yetanotherjnovelreader.seriesnavigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.common.ListItem
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemFragment
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemViewModel
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemViewModelFactory
import com.ytrewqwert.yetanotherjnovelreader.data.Part
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.Series
import com.ytrewqwert.yetanotherjnovelreader.data.Volume
import com.ytrewqwert.yetanotherjnovelreader.partreader.PartActivity
import kotlinx.coroutines.launch

class ExplorerFragment : Fragment() {

    companion object {
        private const val TAG = "NavigationFragment"
    }

    private enum class ListTypes { SERIES, VOLUMES, PARTS }

    private val viewModel by viewModels<ExplorerViewModel> {
        ExplorerViewModelFactory(Repository.getInstance(requireContext()))
    }
    private val listItemViewModel by viewModels<ListItemViewModel> {
        ListItemViewModelFactory(Repository.getInstance(requireContext()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listItemViewModel.itemClickedEvent.observe(this) { onListItemInteraction(it.item) }

        refreshSeriesList()
        setListItemFragment(ListTypes.SERIES.ordinal, ListTypes.SERIES.name)

        listItemViewModel.getRefreshLiveEvent(ListTypes.SERIES.ordinal).observe(this) {
            refreshSeriesList()
        }
        listItemViewModel.getRefreshLiveEvent(ListTypes.VOLUMES.ordinal).observe(this) {
            refreshVolumesList()
        }
        listItemViewModel.getRefreshLiveEvent(ListTypes.PARTS.ordinal).observe(this) {
            refreshPartsList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_explorer, container, false)
    }

    override fun onResume() {
        super.onResume()
        // Propagate to child fragment
        val curFragment = childFragmentManager.findFragmentById(R.id.fragment_container)
        curFragment?.onResume()
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
        listItemViewModel.setItemList(ListTypes.VOLUMES.ordinal, emptyList())
        refreshVolumesList(serie)
        setListItemFragment(ListTypes.VOLUMES.ordinal, ListTypes.VOLUMES.name)
    }
    private fun onVolumesListItemInteraction(volume: Volume) {
        Log.d(TAG, "Volume clicked: ${volume.title}")
        listItemViewModel.setItemList(ListTypes.PARTS.ordinal, emptyList())
        refreshPartsList(volume)
        setListItemFragment(ListTypes.PARTS.ordinal, ListTypes.PARTS.name)
    }
    private fun onPartsListItemInteraction(part: Part) {
        Log.d(TAG, "Part clicked: ${part.title}")
        val intent = Intent(context, PartActivity::class.java)
        intent.putExtra(PartActivity.EXTRA_PART_ID, part.id)
        startActivity(intent)
    }

    private fun refreshSeriesList() {
        lifecycleScope.launch {
            val series = viewModel.getSeries()
            listItemViewModel.setItemList(ListTypes.SERIES.ordinal, series)
        }
    }
    private fun refreshVolumesList(serie: Series? = null) {
        lifecycleScope.launch {
            val volumes = if (serie != null) {
                viewModel.getSerieVolumes(serie)
            } else {
                viewModel.getSerieVolumes()
            }
            listItemViewModel.setItemList(ListTypes.VOLUMES.ordinal, volumes)
        }
    }
    private fun refreshPartsList(volume: Volume? = null) {
        lifecycleScope.launch {
            val parts = if (volume != null) {
                viewModel.getVolumeParts(volume)
            } else {
                viewModel.getVolumeParts()
            }
            listItemViewModel.setItemList(ListTypes.PARTS.ordinal, parts)
        }
    }

    private fun setListItemFragment(fragmentId: Int, fragmentTag: String?) {
        with (childFragmentManager.beginTransaction()) {
            val args = Bundle()
            args.putInt(ListItemFragment.ARG_ID, fragmentId)
            setCustomAnimations(
                R.animator.slide_from_right, R.animator.slide_to_left,
                R.animator.slide_from_left, R.animator.slide_to_right
            )
            replace(
                R.id.fragment_container,
                ListItemFragment::class.java,
                args, fragmentTag
            )
            if (childFragmentManager.findFragmentById(R.id.fragment_container) != null) {
                Log.d(TAG, "adding to back stack")
                addToBackStack(null)
            }
            commit()
        }
    }
}
