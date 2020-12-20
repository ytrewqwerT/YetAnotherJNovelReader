package com.ytrewqwert.yetanotherjnovelreader.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemFragment
import com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.ExplorerFragment

/** An adapter for the ViewPager in the [MainActivity]. */
class MainPagerAdapter(fm: FragmentManager)
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    /** Defines what pages are available and the order they are shown in. */
    enum class ChildFragments {
        RECENT_PARTS,
        EXPLORER,
        UP_NEXT_PARTS
    }

    private val recentPartsFragment by lazy {
        createListItemFragment(ChildFragments.RECENT_PARTS.ordinal)
    }
    private val explorerFragment by lazy { ExplorerFragment() }
    private val upNextPartsFragment by lazy {
        createListItemFragment(ChildFragments.UP_NEXT_PARTS.ordinal)
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

    private fun createListItemFragment(id: Int): ListItemFragment {
        val fragment = ListItemFragment()
        val args = Bundle()
        args.putInt(ListItemFragment.ARG_ID, id)

        // Make part lists compact. (Both calls of this function are for part lists.)
        args.putBoolean(ListItemFragment.ARG_COMPACT, true)

        fragment.arguments = args
        return fragment
    }
}