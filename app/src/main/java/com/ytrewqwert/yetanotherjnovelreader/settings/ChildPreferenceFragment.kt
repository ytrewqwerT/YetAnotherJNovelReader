package com.ytrewqwert.yetanotherjnovelreader.settings

import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat

/** A PreferenceFragment that can be nested inside another fragment instead of an activity. */
abstract class ChildPreferenceFragment : PreferenceFragmentCompat() {
    // Access to this function is supposed to be restricted to the androidx library (for
    // androidx.leanback). i.e. @RestrictTo(LIBRARY_GROUP_PREFIX) in super declaration.
    // But doing this (which is the same as what the leanback library does) allows the fragment to
    // navigate to sub preference screens while nested inside other fragments rather than only as
    // a direct child to an activity.
    override fun getCallbackFragment(): Fragment? = parentFragment
}