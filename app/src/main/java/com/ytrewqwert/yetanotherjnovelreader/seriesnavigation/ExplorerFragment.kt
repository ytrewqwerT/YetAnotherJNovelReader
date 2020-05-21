package com.ytrewqwert.yetanotherjnovelreader.seriesnavigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.common.listitem.ListItem
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemFragment
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemViewModel
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemViewModelFactory
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.SerieFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeFull
import com.ytrewqwert.yetanotherjnovelreader.partreader.PartActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi

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

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setListItemFragment(ListTypes.SERIES.ordinal, ListTypes.SERIES.name)
        observeViewModels()
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

    @ExperimentalCoroutinesApi
    private fun observeViewModels() {
        viewModel.seriesList.observe(this) {
            listItemViewModel.getItemList(ListTypes.SERIES.ordinal).value = it
        }
        viewModel.volumesList.observe(this) {
            listItemViewModel.getItemList(ListTypes.VOLUMES.ordinal).value = it
        }
        viewModel.partsList.observe(this) {
            listItemViewModel.getItemList(ListTypes.PARTS.ordinal).value = it
        }

        listItemViewModel.itemClickedEvent.observe(this) { onListItemInteraction(it.item) }
        listItemViewModel.getIsReloading(ListTypes.SERIES.ordinal).observe(this) {
            if (it) viewModel.fetchSeries {
                listItemViewModel.getIsReloading(ListTypes.SERIES.ordinal).value = false
            }
        }
        listItemViewModel.getIsReloading(ListTypes.VOLUMES.ordinal).observe(this) {
            if (it) viewModel.fetchSerieVolumes {
                listItemViewModel.getIsReloading(ListTypes.VOLUMES.ordinal).value = false
            }
        }
        listItemViewModel.getIsReloading(ListTypes.PARTS.ordinal).observe(this) {
            if (it) viewModel.fetchVolumeParts {
                listItemViewModel.getIsReloading(ListTypes.PARTS.ordinal).value = false
            }
        }
    }

    private fun onListItemInteraction(item: ListItem) {
        when (item) {
            is SerieFull -> onSeriesListItemInteraction(item)
            is VolumeFull -> onVolumesListItemInteraction(item)
            is PartFull -> onPartsListItemInteraction(item)
            else -> Log.e(TAG, "ListItemInteraction for $item's type not handled")
        }
    }

    private fun onSeriesListItemInteraction(serie: SerieFull) {
        Log.d(TAG, "Series clicked: ${serie.serie.title}")
        viewModel.curSerie = serie.serie

        listItemViewModel.let {
            it.getItemList(ListTypes.VOLUMES.ordinal).value = emptyList()
            it.getHeaderList(ListTypes.VOLUMES.ordinal).value = listOf(serie)
            it.getIsReloading(ListTypes.VOLUMES.ordinal).value = true
        }

        setListItemFragment(ListTypes.VOLUMES.ordinal, ListTypes.VOLUMES.name)
    }
    private fun onVolumesListItemInteraction(volume: VolumeFull) {
        Log.d(TAG, "Volume clicked: ${volume.volume.title}")
        viewModel.curVolume = volume.volume

        listItemViewModel.let {
            it.getItemList(ListTypes.PARTS.ordinal).value = emptyList()
            it.getHeaderList(ListTypes.PARTS.ordinal).value = listOf(volume)
            it.getIsReloading(ListTypes.PARTS.ordinal).value = true
        }

        setListItemFragment(ListTypes.PARTS.ordinal, ListTypes.PARTS.name)
    }
    private fun onPartsListItemInteraction(part: PartFull) {
        Log.d(TAG, "Part clicked: ${part.part.title}")
        val intent = Intent(context, PartActivity::class.java)
        intent.putExtra(PartActivity.EXTRA_PART_ID, part.part.id)
        startActivity(intent)
    }

    private fun setListItemFragment(fragmentId: Int, fragmentTag: String?) {
        childFragmentManager.commit {
            val args = Bundle()
            args.putInt(ListItemFragment.ARG_ID, fragmentId)
            setCustomAnimations(
                R.animator.slide_from_right, R.animator.slide_to_left,
                R.animator.slide_from_left, R.animator.slide_to_right
            )
            replace(R.id.fragment_container, ListItemFragment::class.java, args, fragmentTag)
            if (childFragmentManager.findFragmentById(R.id.fragment_container) != null) {
                addToBackStack(null)
            }
        }
    }
}
