package com.ytrewqwert.yetanotherjnovelreader.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.ytrewqwert.yetanotherjnovelreader.R

/** Allows the setting of margin sizes around each edge of the part reader. */
class MarginFragment : ChildPreferenceFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_margin, rootKey)
    }
}
