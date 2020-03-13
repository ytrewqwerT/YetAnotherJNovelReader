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

    @BindingAdapter("app:readerMarginHorizontal")
    @JvmStatic
    fun textViewReaderMarginHorizontal(textView: TextView, sizeDIP: Int) {
        val margin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, sizeDIP.toFloat(), textView.resources.displayMetrics
        ).toInt()
        textView.setPadding(margin, textView.paddingTop, margin, textView.paddingBottom)
    }

    @BindingAdapter("app:readerMarginVertical")
    @JvmStatic
    fun textViewReaderMarginVertical(textView: TextView, sizeDIP: Int) {
        val margin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, sizeDIP.toFloat(), textView.resources.displayMetrics
        ).toInt()
        textView.setPadding(textView.paddingLeft, margin, textView.paddingRight, margin)
    }

    @BindingAdapter("app:readerMarginVertical")
    @JvmStatic
    fun scrollViewReaderMarginVertical(scrollView: ScrollView, sizeDIP: Int) {
        val margin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, sizeDIP.toFloat(), scrollView.resources.displayMetrics
        ).toInt()
        scrollView.setPadding(scrollView.paddingLeft, margin, scrollView.paddingRight, margin)
    }
}