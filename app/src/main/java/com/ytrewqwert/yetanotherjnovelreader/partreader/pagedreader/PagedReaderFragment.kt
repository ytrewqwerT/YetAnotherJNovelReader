package com.ytrewqwert.yetanotherjnovelreader.partreader.pagedreader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.ytrewqwert.yetanotherjnovelreader.BindingAdapters
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.addOnPageSelectedListener
import com.ytrewqwert.yetanotherjnovelreader.databinding.FragmentPagedReaderBinding
import com.ytrewqwert.yetanotherjnovelreader.partreader.PartViewModel

/**
 * A part reader fragment showing the contents of the part in multiple pages that are scrolled
 * through horizontally.
 */
class PagedReaderFragment : Fragment() {
    private val partViewModel by activityViewModels<PartViewModel>()
    private val pagedReaderViewModel by viewModels<PagedReaderViewModel>()

    private var binding: FragmentPagedReaderBinding? = null
    private var pager: ViewPager2? = null
    private val pagerAdapter by lazy { PagedReaderAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_paged_reader, container, false
        )
        binding?.lifecycleOwner = viewLifecycleOwner
        binding?.partViewModel = partViewModel

        val view = binding?.root
        pager = view?.findViewById(R.id.pager)
        pager?.adapter = pagerAdapter
        initialiseObserversListeners()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pager?.adapter = null
        pager = null
    }

    private fun initialiseObserversListeners() {
        partViewModel.contents.observe(viewLifecycleOwner) {
            pagedReaderViewModel.fullContent.value = it
        }

        partViewModel.pageTurn.observe(viewLifecycleOwner) {
            val totalPages = pagerAdapter.itemCount
            val currentPage = pager?.currentItem ?: 0
            val newPage = currentPage + when (it) {
                PartViewModel.PageTurn.TURN_FORWARD -> 1
                PartViewModel.PageTurn.TURN_BACKWARD -> -1
                else -> 0
            }
            pager?.setCurrentItem(newPage.coerceIn(0, totalPages-1), true)
        }

        pagedReaderViewModel.pageCount.observe(viewLifecycleOwner) {
            pagerAdapter.setNumPages(it)
            pager?.let { pager ->
                BindingAdapters.setPagedReaderPosition(pager, partViewModel.currentProgress.value ?: 0.0)
            }
        }

        pager?.addOnPageSelectedListener { position ->
            val numPages = pagerAdapter.itemCount
            // Ignore the one-page scenario which would only result from a really small part
            // displayed on a really large device with an unreasonably small font size and
            // line/paragraph spacing. This is done to allow for a lone page being used in the
            // pagination process without unintentionally setting changing the user's part progress.
            // TODO: Fix this issue, probably via. some separate invisible dummy page.
            if (numPages > 1) {
                partViewModel.currentProgress.value = position.toDouble() / (numPages - 1)
            }
        }
    }
}
