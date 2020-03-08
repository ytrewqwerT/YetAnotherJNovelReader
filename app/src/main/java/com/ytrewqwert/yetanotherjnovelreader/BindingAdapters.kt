package com.ytrewqwert.yetanotherjnovelreader

import android.util.TypedValue
import android.widget.TextView
import androidx.databinding.BindingAdapter

object BindingAdapters {

    @BindingAdapter("android:textSize")
    @JvmStatic
    fun textViewTextSize(textView: TextView, sizeDP: Int) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeDP.toFloat())
    }
}