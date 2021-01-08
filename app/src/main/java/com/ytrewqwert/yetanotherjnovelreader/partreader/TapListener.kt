package com.ytrewqwert.yetanotherjnovelreader.partreader

import android.view.MotionEvent
import android.view.View

/**
 * A [View.OnTouchListener] that only responds tap events (both long and short).
 * @param[ignoreTapCondition] Called on tap down. Ignores the tap if it returns true.
 * @param[onTap] The method to execute on tap release.
 */
class TapListener(
    private val ignoreTapCondition: () -> Boolean = {false},
    private val onTap: (v: View?, event: MotionEvent?) -> Boolean
) : View.OnTouchListener {
    private var prevEventIsDown: Boolean = false

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        var result = false
        if (prevEventIsDown && event?.action == MotionEvent.ACTION_UP) result = onTap(v, event)
        prevEventIsDown = (event?.action == MotionEvent.ACTION_DOWN && !ignoreTapCondition())
        return result
    }
}