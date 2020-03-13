package com.ytrewqwert.yetanotherjnovelreader.partreader.pagedreader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.databinding.FragmentPagedReaderPageBinding
import com.ytrewqwert.yetanotherjnovelreader.partreader.PartViewModel

class PagedReaderPageFragment : Fragment() {
    companion object {
        const val ARG_PAGE_CONTENT = "PAGED_READER_PAGE_CONTENT"
    }

    private var textView: TextView? = null
    private val viewModel by activityViewModels<PartViewModel>()
    private var binding: FragmentPagedReaderPageBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_paged_reader_page, container, false
        )
        binding?.lifecycleOwner = viewLifecycleOwner
        binding?.viewModel = viewModel
        val view = binding?.root
        textView = view?.findViewById(R.id.page_contents)

        val text = requireArguments().getCharSequence(ARG_PAGE_CONTENT) ?: "Page content not given"
        textView?.text = text
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textView = null
    }

}
