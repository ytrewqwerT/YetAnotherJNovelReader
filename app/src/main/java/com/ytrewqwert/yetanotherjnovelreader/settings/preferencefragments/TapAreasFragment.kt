package com.ytrewqwert.yetanotherjnovelreader.settings.preferencefragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.common.RepositoriedViewModelFactory
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.preferences.ReaderPreferenceStore
import com.ytrewqwert.yetanotherjnovelreader.settings.MarginViewPreferenceFragment
import com.ytrewqwert.yetanotherjnovelreader.settings.PreferenceViewModel

class TapAreasFragment : MarginViewPreferenceFragment() {
    private val viewModel by viewModels<PreferenceViewModel> {
        RepositoriedViewModelFactory(Repository.getInstance(requireContext()))
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_page_turn, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.pageTurnAreasPc.observe(viewLifecycleOwner) { pageTurnAreasPc ->
            view.resources?.displayMetrics?.let { displayMetrics ->
                val left   = displayMetrics.widthPixels  * pageTurnAreasPc.left   / 100
                val right  = displayMetrics.widthPixels  * pageTurnAreasPc.right  / 100
                val top    = displayMetrics.heightPixels * pageTurnAreasPc.top    / 100
                val bottom = displayMetrics.heightPixels * pageTurnAreasPc.bottom / 100
                setMarginSizes(ReaderPreferenceStore.Margins(top, bottom, left, right))
            }
        }
    }
}
