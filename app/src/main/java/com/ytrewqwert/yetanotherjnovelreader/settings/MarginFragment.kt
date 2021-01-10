package com.ytrewqwert.yetanotherjnovelreader.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.Utils
import com.ytrewqwert.yetanotherjnovelreader.common.RepositoriedViewModelFactory
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.preferences.ReaderPreferenceStore

/** Allows the setting of margin sizes around each edge of the part reader. */
class MarginFragment : ChildPreferenceFragment() {
    private val viewModel by viewModels<PreferenceViewModel> {
        RepositoriedViewModelFactory(Repository.getInstance(requireContext()))
    }

    private val activityRoot: ViewGroup? get() = activity?.findViewById(R.id.reader_area)
    private var visualMarginView: View? = null
    private val leftMarginView: View? get() = visualMarginView?.findViewById(R.id.left_margin)
    private val rightMarginView: View? get() = visualMarginView?.findViewById(R.id.right_margin)
    private val topMarginView: View? get() = visualMarginView?.findViewById(R.id.top_margin)
    private val bottomMarginView: View? get() = visualMarginView?.findViewById(R.id.bottom_margin)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_margin, rootKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        visualMarginView = inflater.inflate(R.layout.margins_visualisation, activityRoot, false)
        activityRoot?.addView(visualMarginView)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activityRoot?.removeView(visualMarginView)
        visualMarginView = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.margins.observe(viewLifecycleOwner) { margins ->
            updateMarginVisibility(margins)

            visualMarginView?.resources?.displayMetrics?.let { displayMetrics ->
                val leftParams = leftMarginView?.layoutParams
                leftParams?.width = Utils.dpToPx(margins.left, displayMetrics)
                leftMarginView?.layoutParams = leftParams

                val rightParams = rightMarginView?.layoutParams
                rightParams?.width = Utils.dpToPx(margins.right, displayMetrics)
                rightMarginView?.layoutParams = rightParams

                val topParams = topMarginView?.layoutParams
                topParams?.height = Utils.dpToPx(margins.top, displayMetrics)
                topMarginView?.layoutParams = topParams

                val bottomParams = bottomMarginView?.layoutParams
                bottomParams?.height = Utils.dpToPx(margins.bottom, displayMetrics)
                bottomMarginView?.layoutParams = bottomParams
            }
        }
    }

    private fun updateMarginVisibility(margins: ReaderPreferenceStore.Margins) {
        leftMarginView?.visibility   = if (margins.left == 0)   View.GONE else View.VISIBLE
        rightMarginView?.visibility  = if (margins.right == 0)  View.GONE else View.VISIBLE
        topMarginView?.visibility    = if (margins.top == 0)    View.GONE else View.VISIBLE
        bottomMarginView?.visibility = if (margins.bottom == 0) View.GONE else View.VISIBLE
    }
}
