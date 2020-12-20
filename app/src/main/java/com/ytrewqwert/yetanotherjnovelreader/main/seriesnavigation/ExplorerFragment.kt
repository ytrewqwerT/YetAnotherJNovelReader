package com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.common.listitem.ListItem
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemFragment
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemViewModel
import com.ytrewqwert.yetanotherjnovelreader.common.RepositoriedViewModelFactory
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.SerieFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeFull
import com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.serievolumes.SerieVolumesFragment
import com.ytrewqwert.yetanotherjnovelreader.partreader.PartActivity

/** Displays lists of series, volumes and parts allowing for exploration of available content. */
class ExplorerFragment : Fragment() {
    companion object {
        private const val TAG = "NavigationFragment"
    }

    private enum class ListTypes { SERIES, PARTS }

    private val viewModel by viewModels<ExplorerViewModel> {
        RepositoriedViewModelFactory(Repository.getInstance(requireContext()))
    }
    private val listItemViewModel by viewModels<ListItemViewModel> {
        RepositoriedViewModelFactory(Repository.getInstance(requireContext()))
    }

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

    private fun observeViewModels() {
        listItemViewModel.setSource(ListTypes.SERIES.ordinal, viewModel.getSeriesSource())

        listItemViewModel.itemClickedEvent.observe(this) { onListItemInteraction(it.item) }
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

        childFragmentManager.commit {
            val args = Bundle()
            args.putString(SerieVolumesFragment.ARG_SERIE_ID, serie.serie.id)
            setCustomAnimations(
                R.animator.slide_from_right, R.animator.slide_to_left,
                R.animator.slide_from_left, R.animator.slide_to_right
            )
            replace(R.id.fragment_container, SerieVolumesFragment::class.java, args)
            if (childFragmentManager.findFragmentById(R.id.fragment_container) != null) {
                addToBackStack(null)
            }
        }
    }
    fun onVolumesListItemInteraction(volume: VolumeFull) {
        Log.d(TAG, "Volume clicked: ${volume.volume.title}")

        listItemViewModel.setSource(ListTypes.PARTS.ordinal, viewModel.getVolumePartsSource(volume))
        listItemViewModel.setHeader(ListTypes.PARTS.ordinal, volume)
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
