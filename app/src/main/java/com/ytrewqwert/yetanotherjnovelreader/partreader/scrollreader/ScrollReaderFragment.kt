package com.ytrewqwert.yetanotherjnovelreader.partreader.scrollreader

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.databinding.FragmentScrollReaderBinding
import com.ytrewqwert.yetanotherjnovelreader.partreader.PartViewModel
import com.ytrewqwert.yetanotherjnovelreader.partreader.TapListener

/** A part reader fragment showing the contents of the part as a single long vertical strip. */
class ScrollReaderFragment : Fragment() {

    private val viewModel by activityViewModels<PartViewModel>()

    private var binding: FragmentScrollReaderBinding? = null
    private var scrollView: ScrollView? = null
    private var textView: TextView? = null
    private var lastScrollTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_scroll_reader, container, false
        )
        binding?.lifecycleOwner = viewLifecycleOwner
        binding?.viewModel = viewModel

        val view = binding?.root
        scrollView = view?.findViewById(R.id.content_scroll_container)
        textView = view?.findViewById(R.id.content_view)

        scrollView?.setOnScrollChangeListener { _, _, y, _, oldY ->
            lastScrollTime = System.currentTimeMillis()
            viewModel.currentProgress.value = getScrollPercentage()
        }

        // Detect reader screen taps and notify the viewModel of it's location for processing.
        // Ignore the tap if it was done to stop the scrollView's scrolling (or rather, ignore if
        // the scroll position changed recently.
        scrollView?.setOnTouchListener(TapListener(
            requireContext(),
            ignoreTapCondition = { System.currentTimeMillis() - lastScrollTime < 50 },
            onTap = { event ->
                val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                val screenWidth = Resources.getSystem().displayMetrics.widthPixels
                // The values sometimes go slightly over 1.0, so coerce to the expected range.
                val x = ((event?.rawX ?: 0f) / screenWidth).coerceIn(0f, 1f)
                val y = ((event?.rawY ?: 0f) / screenHeight).coerceIn(0f, 1f)
                viewModel.processScreenTap(x, y)
                true
            }
        ))

        viewModel.pageTurn.observe(viewLifecycleOwner) {
            // Scroll the full page height minus the height of one line of text to avoid partially
            // obscured lines from being skipped.
            val pageHeight = scrollView?.height ?: 0
            val lineHeight = textView?.lineHeight ?: 0
            val scrollDistance = (pageHeight - lineHeight).coerceAtLeast(0)

            val newPos = (scrollView?.scrollY ?: 0) + when (it) {
                PartViewModel.PageTurn.TURN_FORWARD -> scrollDistance
                PartViewModel.PageTurn.TURN_BACKWARD -> -scrollDistance
                else -> 0
            }
            scrollView?.smoothScrollTo(scrollView?.scrollX ?: 0, newPos)
        }

        // Indirectly notify scrollView (via databinding) to update its position when textView's
        // layout changes.
        textView?.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            viewModel.currentProgress.value = viewModel.currentProgress.value
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        scrollView = null
        textView = null
    }

    private fun getScrollPercentage(): Double {
        val tvHeight = textView?.height ?: 0
        val svHeight = scrollView?.height ?: 0
        val scrollY = scrollView?.scrollY ?: 0
        return if (svHeight < tvHeight) {
            // tvHeight - svHeight == scrollY when scrolled to bottom of scrollView
            scrollY.toDouble() / (tvHeight - svHeight)
        } else {
            1.0
        }
    }
}
