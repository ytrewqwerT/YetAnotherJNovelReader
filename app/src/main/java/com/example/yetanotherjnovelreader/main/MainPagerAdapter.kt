package com.example.yetanotherjnovelreader.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.yetanotherjnovelreader.common.ListItemFragment
import com.example.yetanotherjnovelreader.seriesnavigation.ExplorerFragment

class MainPagerAdapter(fm: FragmentManager)
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    enum class ChildFragments {
        RECENT_PARTS,
        EXPLORER
    }

    private val recentPartsFragment by lazy {
        val fragment = ListItemFragment()
        val args = Bundle()
        args.putInt(ListItemFragment.ARG_ID, ChildFragments.RECENT_PARTS.ordinal)
        fragment.arguments = args
        fragment
    }
    private val explorerFragment by lazy { ExplorerFragment() }

    override fun getItem(position: Int): Fragment {
        return when (ChildFragments.values()[position]) {
            ChildFragments.RECENT_PARTS -> recentPartsFragment
            ChildFragments.EXPLORER -> explorerFragment
        }
    }

    override fun getCount(): Int = ChildFragments.values().size
}