package com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.serievolumes

import androidx.fragment.app.viewModels
import com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist.SwipeableListFragment
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeFull

// TODO: Add header that allows for series follow/unfollow.
// TODO: Ensure all parts in a volume is loaded to the database for showing volume progress.
class SerieVolumesFragment
    : SwipeableListFragment<VolumeFull>(), ListVolumeRecyclerViewAdapter.ClickListener {

    private val serieId by lazy { arguments?.getString(ARG_SERIE_ID) ?: "" }

    override val listContentsAdapter by lazy {
        ListVolumeRecyclerViewAdapter(this, this)
    }
    override val viewModel by viewModels<SerieVolumesViewModel> {
        SerieVolumesViewModelFactory(Repository.getInstance(requireContext()), serieId)
    }


    override fun onVolumeClick(volume: VolumeFull) {
        TODO("Not yet implemented")
    }

    companion object {
        const val ARG_SERIE_ID = "SERIE_ID"
    }
}