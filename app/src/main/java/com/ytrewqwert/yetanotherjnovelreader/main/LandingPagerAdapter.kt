package com.ytrewqwert.yetanotherjnovelreader.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ytrewqwert.yetanotherjnovelreader.main.partslists.recentpartslist.RecentPartsListFragment
import com.ytrewqwert.yetanotherjnovelreader.main.partslists.upnextpartslist.UpNextPartsListFragment
import com.ytrewqwert.yetanotherjnovelreader.main.serieslist.SeriesListFragment

/** An adapter for the ViewPager in the [MainActivity]. */
class LandingPagerAdapter(fm: FragmentManager)
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    /** Defines what pages are available and the order they are shown in. */
    enum class ChildFragments {
        RECENT_PARTS,
        SERIES,
        UP_NEXT_PARTS
    }

    private val recentPartsFragment by lazy { RecentPartsListFragment() }
    private val seriesFragment by lazy { SeriesListFragment() }
    private val upNextPartsFragment by lazy { UpNextPartsListFragment() }

    override fun getCount(): Int = ChildFragments.values().size

    override fun getItem(position: Int): Fragment {
        return when (ChildFragments.values()[position]) {
            ChildFragments.RECENT_PARTS -> recentPartsFragment
            ChildFragments.SERIES -> seriesFragment
            ChildFragments.UP_NEXT_PARTS -> upNextPartsFragment
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (ChildFragments.values()[position]) {
            ChildFragments.RECENT_PARTS -> "Recent"
            ChildFragments.SERIES -> "Series"
            ChildFragments.UP_NEXT_PARTS -> "Up Next"
        }
    }
}