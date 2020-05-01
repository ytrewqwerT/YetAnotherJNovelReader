package com.ytrewqwert.yetanotherjnovelreader.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.ytrewqwert.yetanotherjnovelreader.R

class MarginFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_margin, rootKey)
    }
}
