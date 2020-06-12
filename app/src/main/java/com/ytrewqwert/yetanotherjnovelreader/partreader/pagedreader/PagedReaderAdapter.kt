package com.ytrewqwert.yetanotherjnovelreader.partreader.pagedreader

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/** A pager adapter for [PagedReaderFragment] managing each individual page. */
class PagedReaderAdapter(
    frag: Fragment
) : FragmentStateAdapter(frag) {

    private var numPages = 1 // Initially a single empty page
    override fun getItemCount(): Int = numPages

    override fun createFragment(position: Int): Fragment {
        val fragment = PagedReaderPageFragment()
        val bundle = Bundle()
        bundle.putInt(PagedReaderPageFragment.ARG_PAGE_NUM, position)
        fragment.arguments = bundle
        return fragment
    }

    fun setNumPages(num: Int) {
        numPages = num
        notifyDataSetChanged()
    }
}