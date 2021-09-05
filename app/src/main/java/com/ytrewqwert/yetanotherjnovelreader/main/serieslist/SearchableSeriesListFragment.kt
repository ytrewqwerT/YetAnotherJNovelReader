package com.ytrewqwert.yetanotherjnovelreader.main.serieslist

import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.motion.widget.MotionLayout
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

    private var searchBarVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Piggyback off of the existing menu (MainActivity's) rather than create our own...
        // Show the (by default hidden) search icon in the menu (if it exists...)
        val searchButton = menu.findItem(R.id.search) ?: return
        searchButton.isVisible = true
        searchButton.setOnMenuItemClickListener {
            (view as? MotionLayout)?.let {
                val searchView = it.findViewById<View>(R.id.search_field)
                val imm = requireContext().getSystemService(InputMethodManager::class.java)
                when (searchBarVisible) {
                    true  -> {
                        it.transitionToStart()
                        searchView.clearFocus()
                        imm.hideSoftInputFromWindow(searchView.windowToken, 0)
                    }
                    false -> {
                        it.transitionToEnd()
                        searchView.requestFocus()
                        imm.showSoftInput(searchView, 0)
                    }
                }
                searchBarVisible = !searchBarVisible
            }
            true
        }
    }
}