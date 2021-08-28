package com.ytrewqwert.yetanotherjnovelreader.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.viewpager.widget.ViewPager
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.addOnPageSelectedListener

/** Displays lists of series, volumes and parts allowing for exploration of available content. */
class LandingFragment : Fragment() {
    private var viewPager: ViewPager? = null
    private val activePagerFragment: Fragment? get() = childFragmentManager.findFragmentByTag(
        "android:switcher:${R.id.pager}:${viewPager?.currentItem}"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_landing, container, false)
        viewPager = view.findViewById(R.id.pager)
        val viewPagerAdapter = LandingPagerAdapter(childFragmentManager)
        viewPager?.adapter = viewPagerAdapter

        // Set primary navigation fragment to the focused viewpager page to allow touch interception
        viewPager?.addOnPageSelectedListener {
            childFragmentManager.commit {
                setPrimaryNavigationFragment(activePagerFragment)
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Propagate to child fragment
        val curFragment = childFragmentManager.findFragmentById(R.id.fragment_container)
        curFragment?.onResume()
    }

    override fun onDestroyView() {
        viewPager?.adapter = null
        viewPager = null
        super.onDestroyView()
    }
}
