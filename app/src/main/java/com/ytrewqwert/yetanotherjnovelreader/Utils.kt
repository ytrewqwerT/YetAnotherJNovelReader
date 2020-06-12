package com.ytrewqwert.yetanotherjnovelreader

import android.util.DisplayMetrics
import android.util.TypedValue

/** Random utility functions that would otherwise be free-floating. */
object Utils {
    /** Converts the given [dp] metric to px based on the [displayMetrics]. */
    fun dpToPx(dp: Int, displayMetrics: DisplayMetrics) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), displayMetrics).toInt()
    /** Converts the given [sp] metric to px based on the [displayMetrics]. */
    fun spToPx(sp: Int, displayMetrics: DisplayMetrics) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), displayMetrics).toInt()
}