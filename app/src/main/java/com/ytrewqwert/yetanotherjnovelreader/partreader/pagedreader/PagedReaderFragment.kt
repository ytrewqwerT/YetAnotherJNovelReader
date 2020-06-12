package com.ytrewqwert.yetanotherjnovelreader.partreader.pagedreader

import android.os.Bundle
import android.text.TextPaint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.viewpager2.widget.ViewPager2
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.Utils
import com.ytrewqwert.yetanotherjnovelreader.addOnPageSelectedListener
import com.ytrewqwert.yetanotherjnovelreader.data.local.preferences.PrefDefaults
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
            pagedReaderViewModel.pageWidth = partViewModel.pageWidthPx
            pagedReaderViewModel.pageHeight = partViewModel.pageHeightPx
            val fontSize = partViewModel.fontSize.value ?: PrefDefaults.FONT_SIZE
            val fontPx = Utils.spToPx(fontSize, resources.displayMetrics)
            pagedReaderViewModel.paint = TextPaint().apply {
                textSize = fontPx.toFloat()
                typeface = partViewModel.fontStyle.value
            }
            val numPages = pagedReaderViewModel.setContents(it)
            pagerAdapter.setNumPages(numPages)
        }

        pager?.addOnPageSelectedListener { position ->
            val numPages = pagerAdapter.itemCount
            partViewModel.currentProgress.value = if (numPages > 1) {
                position.toDouble() / (numPages - 1)
            } else {
                1.0
            }
        }
    }
}
