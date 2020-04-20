package com.ytrewqwert.yetanotherjnovelreader.partreader.pagedreader

import android.os.Bundle
import android.text.TextPaint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.viewpager2.widget.ViewPager2
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.addOnPageSelectedListener
import com.ytrewqwert.yetanotherjnovelreader.databinding.FragmentPagedReaderBinding
import com.ytrewqwert.yetanotherjnovelreader.partreader.PartViewModel

class PagedReaderFragment : Fragment() {

    private var binding: FragmentPagedReaderBinding? = null
    private val partViewModel by activityViewModels<PartViewModel>()

    private var pager: ViewPager2? = null
    private val pagerAdapter by lazy { PagedReaderAdapter(requireActivity()) }

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
        pager = null
    }

    private fun initialiseObserversListeners() {
        partViewModel.contents.observe(viewLifecycleOwner) {
            val width = partViewModel.pageWidthPx
            val height = partViewModel.pageHeightPx
            val paint = TextPaint().apply {
                textSize = partViewModel.fontSizePx.toFloat()
                typeface = partViewModel.fontStyle.value
            }
            pagerAdapter.setReaderContents(it, width, height, paint)
        }

        pager?.addOnPageSelectedListener { position ->
            val numPages = pagerAdapter.itemCount
            partViewModel.currentProgress.value = position.toDouble() / numPages
        }
    }
}
