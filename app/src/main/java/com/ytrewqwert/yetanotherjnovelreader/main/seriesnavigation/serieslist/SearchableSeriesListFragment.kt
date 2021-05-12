package com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.serieslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.common.RepositoriedViewModelFactory
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.databinding.FragmentSearchableListBinding

class SearchableSeriesListFragment : Fragment() {
    private val viewModel by viewModels<SeriesListViewModel> {
        RepositoriedViewModelFactory(Repository.getInstance(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSearchableListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        childFragmentManager.commit {
            replace(R.id.list_container, SeriesListFragment::class.java, null, "series_list_fragment")
        }
        return binding.root
    }
}