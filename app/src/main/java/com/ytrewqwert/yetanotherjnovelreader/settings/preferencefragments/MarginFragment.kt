package com.ytrewqwert.yetanotherjnovelreader.settings.preferencefragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.Utils
import com.ytrewqwert.yetanotherjnovelreader.common.RepositoriedViewModelFactory
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.preferences.ReaderPreferenceStore
import com.ytrewqwert.yetanotherjnovelreader.settings.MarginViewPreferenceFragment
import com.ytrewqwert.yetanotherjnovelreader.settings.PreferenceViewModel

/** Allows the setting of margin sizes around each edge of the part reader. */
class MarginFragment : MarginViewPreferenceFragment() {
    private val viewModel by viewModels<PreferenceViewModel> {
        RepositoriedViewModelFactory(Repository.getInstance(requireContext()))
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_margin, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.marginsDp.observe(viewLifecycleOwner) { marginsDp ->
            view.resources?.displayMetrics?.let { displayMetrics ->
                val left = Utils.dpToPx(marginsDp.left, displayMetrics)
                val right = Utils.dpToPx(marginsDp.right, displayMetrics)
                val top = Utils.dpToPx(marginsDp.top, displayMetrics)
                val bottom = Utils.dpToPx(marginsDp.bottom, displayMetrics)
                setMarginSizes(ReaderPreferenceStore.Margins(top, bottom, left, right))
            }
        }
    }
}
