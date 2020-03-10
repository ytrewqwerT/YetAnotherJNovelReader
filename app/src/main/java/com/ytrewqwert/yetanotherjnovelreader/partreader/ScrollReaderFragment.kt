package com.ytrewqwert.yetanotherjnovelreader.partreader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.databinding.FragmentScrollReaderBinding

class ScrollReaderFragment : Fragment() {

    private var binding: FragmentScrollReaderBinding? = null
    private val viewModel by activityViewModels<PartViewModel>()

    private var scrollView: ScrollView? = null
    private var textView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_scroll_reader, null, false
        )
        binding?.lifecycleOwner = viewLifecycleOwner
        binding?.viewModel = viewModel

        val view = binding?.root
        scrollView = view?.findViewById(R.id.content_scroll_container)
        textView = view?.findViewById(R.id.content_view)
        initialiseObserversListeners()

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

    private fun initialiseObserversListeners() {
        viewModel.gotoProgressEvent.observe(viewLifecycleOwner) {
            scrollToPosition(viewModel.currentProgress.value ?: 0.0)
        }
        scrollView?.setOnScrollChangeListener { _, _, _, _, _ ->
            viewModel.currentProgress.value = getScrollPercentage()
        }
        textView?.setOnClickListener {
            viewModel.showAppBar.value = !(viewModel.showAppBar.value ?: false)
        }
    }

    private fun scrollToPosition(percentage: Double) {
        val tvHeight = textView?.height ?: 0
        val svHeight = scrollView?.height ?: 0
        val position = (tvHeight - svHeight) * percentage
        scrollView?.scrollTo(0, position.toInt())
    }
}
