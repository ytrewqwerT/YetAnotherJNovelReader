package com.ytrewqwert.yetanotherjnovelreader.settings.preferencefragments

import android.os.Bundle
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.settings.ChildPreferenceFragment

/** The main settings fragment allowing the setting of user preferences. */
class MainFragment : ChildPreferenceFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}