package com.ytrewqwert.yetanotherjnovelreader

import android.util.DisplayMetrics
import android.util.TypedValue

object Utils {
    fun dpToPx(dp: Int, displayMetrics: DisplayMetrics) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), displayMetrics).toInt()
}