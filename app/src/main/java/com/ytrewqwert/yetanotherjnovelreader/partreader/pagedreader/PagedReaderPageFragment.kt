package com.ytrewqwert.yetanotherjnovelreader.partreader.pagedreader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ytrewqwert.yetanotherjnovelreader.R

class PagedReaderPageFragment : Fragment() {

    private var pageId: Int = -1
    private var textView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_paged_reader_page, container, false)
        textView = view.findViewById(R.id.page_contents)
        val text = "pageId $pageId" // TODO: temp text
        textView?.text = text
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textView = null
    }

}
