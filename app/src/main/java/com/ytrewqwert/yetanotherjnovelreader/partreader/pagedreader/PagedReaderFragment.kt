package com.ytrewqwert.yetanotherjnovelreader.partreader.pagedreader

import android.os.Bundle
import android.text.TextPaint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.viewpager2.widget.ViewPager2
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.partreader.PartViewModel

class PagedReaderFragment : Fragment() {

    private val partViewModel by activityViewModels<PartViewModel>()

    private var pager: ViewPager2? = null
    private val pagerAdapter by lazy { PagedReaderAdapter(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_paged_reader, container, false)
        pager = view.findViewById(R.id.pager)
        pager?.adapter = pagerAdapter
        observeViewModel()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pager?.adapter = null
        pager = null
    }

    private fun observeViewModel() {
        partViewModel.contents.observe(viewLifecycleOwner) {
            val width = partViewModel.pageWidthPx
            val height = partViewModel.pageHeightPx
            val paint = TextPaint().apply {
                textSize = partViewModel.fontSizePx.toFloat()
                typeface = partViewModel.fontStyle.value
            }
            pagerAdapter.setReaderContents(it, width, height, paint)
        }
    }
}
