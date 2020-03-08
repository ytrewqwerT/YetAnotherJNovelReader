package com.ytrewqwert.yetanotherjnovelreader

import android.util.TypedValue
import android.widget.ScrollView
import android.widget.TextView
import androidx.databinding.BindingAdapter

object BindingAdapters {

    @BindingAdapter("android:textSize")
    @JvmStatic
    fun textViewTextSize(textView: TextView, sizeDP: Int) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeDP.toFloat())
    }

    @BindingAdapter("app:readerMargin")
    @JvmStatic
    fun textViewReaderMargin(textView: TextView, sizeDIP: Int) {
        val margin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, sizeDIP.toFloat(), textView.resources.displayMetrics
        ).toInt()
        textView.setPadding(margin, 0, margin, 0)
    }

    @BindingAdapter("app:readerMargin")
    @JvmStatic
    fun scrollViewReaderMargin(scrollView: ScrollView, sizeDIP: Int) {
        val margin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, sizeDIP.toFloat(), scrollView.resources.displayMetrics
        ).toInt()
        scrollView.setPadding(0, margin, 0, margin)
    }
}