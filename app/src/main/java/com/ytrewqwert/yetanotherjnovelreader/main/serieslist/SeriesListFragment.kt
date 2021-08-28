package com.ytrewqwert.yetanotherjnovelreader.main.serieslist

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.ytrewqwert.yetanotherjnovelreader.common.RepositoriedViewModelFactory
import com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist.SwipeableListFragment
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.SerieFull
import com.ytrewqwert.yetanotherjnovelreader.main.MainActivity
import com.ytrewqwert.yetanotherjnovelreader.main.MainViewModel

/** SwipeableListFragment for showing available series. */
class SeriesListFragment : SwipeableListFragment<SerieFull>(), SeriesListAdapter.Listener {

    override val listContentsAdapter by lazy {
        SeriesListAdapter(this, this)
    }

    override val viewModel by viewModels<SeriesListViewModel> {
        RepositoriedViewModelFactory(Repository.getInstance(requireContext()))
    }
    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onSerieClick(serie: SerieFull) {
        (activity as? MainActivity)?.setChildFragmentToSerie(serie.serie.id) // Hmmm...
    }

    override fun onSerieFollowClick(serie: SerieFull) {
        mainViewModel.toggleFollow(serie.serie.id)
    }
}