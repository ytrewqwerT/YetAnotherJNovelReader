package com.ytrewqwert.yetanotherjnovelreader.seriesnavigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.common.ListItem
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemFragment
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemViewModel
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemViewModelFactory
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.PartFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.SerieFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.VolumeFull
import com.ytrewqwert.yetanotherjnovelreader.partreader.PartActivity

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

        viewModel.fetchSeries()
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

    private fun observeViewModels() {
        viewModel.seriesList.observe(this) {
            listItemViewModel.setItemList(ListTypes.SERIES.ordinal, it)
        }
        viewModel.volumesList.observe(this) {
            listItemViewModel.setItemList(ListTypes.VOLUMES.ordinal, it)
        }
        viewModel.partsList.observe(this) {
            listItemViewModel.setItemList(ListTypes.PARTS.ordinal, it)
        }

        listItemViewModel.itemClickedEvent.observe(this) { onListItemInteraction(it.item) }
        listItemViewModel.getIsReloading(ListTypes.SERIES.ordinal).observe(this) {
            if (it) viewModel.fetchSeries {
                listItemViewModel.setIsReloading(ListTypes.SERIES.ordinal, false)
            }
        }
        listItemViewModel.getIsReloading(ListTypes.VOLUMES.ordinal).observe(this) {
            if (it) viewModel.fetchSerieVolumes {
                listItemViewModel.setIsReloading(ListTypes.VOLUMES.ordinal, false)
            }
        }
        listItemViewModel.getIsReloading(ListTypes.PARTS.ordinal).observe(this) {
            if (it) viewModel.fetchVolumeParts {
                listItemViewModel.setIsReloading(ListTypes.PARTS.ordinal, false)
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
        listItemViewModel.setItemList(ListTypes.VOLUMES.ordinal, emptyList())
        viewModel.curSerie = serie.serie
        listItemViewModel.setIsReloading(ListTypes.VOLUMES.ordinal, true)
        setListItemFragment(ListTypes.VOLUMES.ordinal, ListTypes.VOLUMES.name)
    }
    private fun onVolumesListItemInteraction(volume: VolumeFull) {
        Log.d(TAG, "Volume clicked: ${volume.volume.title}")
        listItemViewModel.setItemList(ListTypes.PARTS.ordinal, emptyList())
        viewModel.curVolume = volume.volume
        listItemViewModel.setIsReloading(ListTypes.PARTS.ordinal, true)
        setListItemFragment(ListTypes.PARTS.ordinal, ListTypes.PARTS.name)
    }
    private fun onPartsListItemInteraction(part: PartFull) {
        Log.d(TAG, "Part clicked: ${part.part.title}")
        val intent = Intent(context, PartActivity::class.java)
        intent.putExtra(PartActivity.EXTRA_PART_ID, part.part.id)
        startActivity(intent)
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
