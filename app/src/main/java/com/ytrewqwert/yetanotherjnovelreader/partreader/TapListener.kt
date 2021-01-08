package com.ytrewqwert.yetanotherjnovelreader.partreader

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat

/**
 * A [View.OnTouchListener] that only responds tap events (both long and short).
 * @param[context] The context in which this is being used.
 * @param[ignoreTapCondition] Called on tap down. Ignores the tap if it returns true.
 * @param[onTap] The method to execute on tap release.
 */
class TapListener(
    context: Context,
    private val ignoreTapCondition: () -> Boolean = {false},
    private val onTap: (event: MotionEvent?) -> Boolean
) : GestureDetector.SimpleOnGestureListener(), View.OnTouchListener {
    private val gestureDetector = GestureDetectorCompat(context, this)

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return !ignoreTapCondition()
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return onTap(e)
    }
}