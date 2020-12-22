package com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.serievolumeslist

import androidx.fragment.app.viewModels
import com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist.SwipeableListFragment
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeFull
import com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.ExplorerFragment

/** SwipeableListFragment for showing the volumes in a series. */
class SerieVolumesListFragment
    : SwipeableListFragment<VolumeFull>(), VolumesListAdapter.Listener {

    private val serieId by lazy { arguments?.getString(ARG_SERIE_ID) ?: "" }

    override val listContentsAdapter by lazy {
        VolumesListAdapter(this, this)
    }
    override val viewModel by viewModels<SerieVolumesListViewModel> {
        SerieVolumesListViewModelFactory(Repository.getInstance(requireContext()), serieId)
    }


    override fun onVolumeClick(volume: VolumeFull) {
        (parentFragment as? ExplorerFragment)?.onVolumesListItemInteraction(volume) // Hmmm...
    }

    override fun onBindVolume(volume: VolumeFull) {
        viewModel.fetchVolumeParts(volume.volume.id)
    }

    override fun onFollowClick() {
        viewModel.toggleFollow()
    }

    companion object {
        const val ARG_SERIE_ID = "SERIE_ID"
    }
}