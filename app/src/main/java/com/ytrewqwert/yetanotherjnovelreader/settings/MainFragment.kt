package com.ytrewqwert.yetanotherjnovelreader.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.ytrewqwert.yetanotherjnovelreader.R

/** The main settings fragment allowing the setting of user preferences. */
class MainFragment : ChildPreferenceFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}