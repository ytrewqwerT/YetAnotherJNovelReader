package com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.series

import androidx.fragment.app.viewModels
import com.ytrewqwert.yetanotherjnovelreader.common.RepositoriedViewModelFactory
import com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist.SwipeableListFragment
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.SerieFull
import com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.ExplorerFragment

class SeriesFragment : SwipeableListFragment<SerieFull>(), SeriesAdapter.Listener {

    override val listContentsAdapter by lazy {
        SeriesAdapter(this, this)
    }
    override val viewModel by viewModels<SeriesViewModel> {
        RepositoriedViewModelFactory(Repository.getInstance(requireContext()))
    }

    override fun onSerieClick(serie: SerieFull) {
        (parentFragment as? ExplorerFragment)?.onSeriesListItemInteraction(serie) // Hmmm...
    }

    override fun onSerieFollowClick(serie: SerieFull) {
        viewModel.toggleFollow(serie)
    }
}