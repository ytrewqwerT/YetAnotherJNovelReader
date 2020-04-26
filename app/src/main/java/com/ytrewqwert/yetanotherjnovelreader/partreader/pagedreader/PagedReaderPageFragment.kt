package com.ytrewqwert.yetanotherjnovelreader.partreader.pagedreader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.databinding.FragmentPagedReaderPageBinding
import com.ytrewqwert.yetanotherjnovelreader.partreader.PartViewModel

class PagedReaderPageFragment : Fragment() {
    companion object {
        const val ARG_PAGE_NUM = "PAGED_READER_PAGE_NUM"
    }

    private var textView: TextView? = null
    private val partViewModel by activityViewModels<PartViewModel>()
    private val pagedReaderViewModel by viewModels<PagedReaderViewModel>(
        ownerProducer = { requireParentFragment() }
    )
    private var binding: FragmentPagedReaderPageBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_paged_reader_page, container, false
        )
        binding?.lifecycleOwner = viewLifecycleOwner
        binding?.viewModel = partViewModel
        val view = binding?.root
        textView = view?.findViewById(R.id.page_contents)

        textView?.setOnClickListener {
            partViewModel.toggleAppBarVisibility()
        }

        val pageNum = requireArguments().getInt(ARG_PAGE_NUM, 0)
        pagedReaderViewModel.getPageContent(pageNum)?.observe(viewLifecycleOwner) {
            textView?.text = it
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textView = null
    }
}
