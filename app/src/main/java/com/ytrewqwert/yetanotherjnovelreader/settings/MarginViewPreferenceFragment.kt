package com.ytrewqwert.yetanotherjnovelreader.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.data.local.preferences.ReaderPreferenceStore

/**
 * A ChildPreferenceFragment that also overlays adjustable margin indicators over the background
 * activity
 */
abstract class MarginViewPreferenceFragment : ChildPreferenceFragment() {

    private val activityRoot: ViewGroup? get() = activity?.findViewById(R.id.reader_area)
    private var visualMarginView: View? = null
    private val leftMarginView: View? get() = visualMarginView?.findViewById(R.id.left_margin)
    private val rightMarginView: View? get() = visualMarginView?.findViewById(R.id.right_margin)
    private val topMarginView: View? get() = visualMarginView?.findViewById(R.id.top_margin)
    private val bottomMarginView: View? get() = visualMarginView?.findViewById(R.id.bottom_margin)

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

    /** Sets the size of the margin indicators from each edge based on [marginsPx]. */
    protected fun setMarginSizes(marginsPx: ReaderPreferenceStore.Margins) {
        updateMarginVisibility(marginsPx)
        val leftParams = leftMarginView?.layoutParams
        leftParams?.width = marginsPx.left
        leftMarginView?.layoutParams = leftParams

        val rightParams = rightMarginView?.layoutParams
        rightParams?.width = marginsPx.right
        rightMarginView?.layoutParams = rightParams

        val topParams = topMarginView?.layoutParams
        topParams?.height = marginsPx.top
        topMarginView?.layoutParams = topParams

        val bottomParams = bottomMarginView?.layoutParams
        bottomParams?.height = marginsPx.bottom
        bottomMarginView?.layoutParams = bottomParams
    }

    private fun updateMarginVisibility(marginsPx: ReaderPreferenceStore.Margins) {
        leftMarginView?.visibility   = if (marginsPx.left == 0)   View.GONE else View.VISIBLE
        rightMarginView?.visibility  = if (marginsPx.right == 0)  View.GONE else View.VISIBLE
        topMarginView?.visibility    = if (marginsPx.top == 0)    View.GONE else View.VISIBLE
        bottomMarginView?.visibility = if (marginsPx.bottom == 0) View.GONE else View.VISIBLE
    }
}