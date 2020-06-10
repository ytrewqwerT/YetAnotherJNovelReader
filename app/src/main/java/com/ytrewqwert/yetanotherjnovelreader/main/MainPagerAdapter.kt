package com.ytrewqwert.yetanotherjnovelreader.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemFragment
import com.ytrewqwert.yetanotherjnovelreader.seriesnavigation.ExplorerFragment

class MainPagerAdapter(fm: FragmentManager)
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    enum class ChildFragments {
        RECENT_PARTS,
        EXPLORER,
        UP_NEXT_PARTS
    }

    private val recentPartsFragment by lazy {
        val fragment = ListItemFragment()
        val args = Bundle()
        args.putInt(ListItemFragment.ARG_ID, ChildFragments.RECENT_PARTS.ordinal)
        fragment.arguments = args
        fragment
    }
    private val explorerFragment by lazy { ExplorerFragment() }
    private val upNextPartsFragment by lazy {
        val fragment = ListItemFragment()
        val args = Bundle()
        args.putInt(ListItemFragment.ARG_ID, ChildFragments.UP_NEXT_PARTS.ordinal)
        fragment.arguments = args
        fragment
    }

    override fun getCount(): Int = ChildFragments.values().size

    override fun getItem(position: Int): Fragment {
        return when (ChildFragments.values()[position]) {
            ChildFragments.RECENT_PARTS -> recentPartsFragment
            ChildFragments.EXPLORER -> explorerFragment
            ChildFragments.UP_NEXT_PARTS -> upNextPartsFragment
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (ChildFragments.values()[position]) {
            ChildFragments.RECENT_PARTS -> "Recent"
            ChildFragments.EXPLORER -> "Explore"
            ChildFragments.UP_NEXT_PARTS -> "Up Next"
        }
    }
}