package com.ytrewqwert.yetanotherjnovelreader.main.serievolumeslist

import android.view.LayoutInflater
import android.widget.LinearLayout
import com.ytrewqwert.yetanotherjnovelreader.R
import kotlinx.android.synthetic.main.list_volume_part_progress.view.*

/** Handles the children in a [LinearLayout] to create a segmented progress bar. */
class SegmentedProgressViewManager {
    private var layout: LinearLayout? = null
    private var progress: List<Double>? = null

    /** Sets the [LinearLayout] to be handled. [newLayout] should have no children. */
    fun setLayout(newLayout: LinearLayout?) {
        layout = newLayout
        refreshLayout()
    }

    /**
     * Sets the progress to be shown in each segment. The number of segments is automatically
     * changed to match the size of the list.
     */
    fun setProgress(newProgress: List<Double>?) {
        progress = newProgress
        refreshLayout()
    }

    private fun refreshLayout() {
        layout?.let { layout ->
            val progress = progress

            // Align layout child count with volume part count
            val size = progress?.size ?: 0
            val inflater = LayoutInflater.from(layout.context)
            while (layout.childCount > size)
                layout.removeViewAt(layout.childCount - 1)
            while (layout.childCount < size) {
                val view = inflater.inflate(R.layout.list_volume_part_progress, layout, false)
                layout.addView(view)
                val params = view.layoutParams as LinearLayout.LayoutParams
                params.weight = 1.0f
                view.layoutParams = params
            }

            for (i in 0 until size) {
                val progressView = layout.getChildAt(i).progress
                    ?: throw NullPointerException("Invalid view found in attached layout")

                val partProgressPercent = (progress?.getOrNull(i) ?: 0.0) * 100
                progressView.progress = partProgressPercent.toInt()
            }
        }
    }
}