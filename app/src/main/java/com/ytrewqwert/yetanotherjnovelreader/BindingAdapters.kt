package com.ytrewqwert.yetanotherjnovelreader

import android.util.TypedValue
import android.widget.ScrollView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ytrewqwert.yetanotherjnovelreader.data.local.PreferenceStore

object BindingAdapters {

    @BindingAdapter("android:textSize")
    @JvmStatic
    fun textViewTextSize(textView: TextView, sizeDP: Int) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeDP.toFloat())
    }

    @BindingAdapter("app:readerMarginHorizontal")
    @JvmStatic
    fun textViewReaderMarginHorizontal(textView: TextView, marginsDIP: PreferenceStore.Margins) {
        val displayMetrics = textView.resources.displayMetrics
        val marginLeft = Utils.dpToPx(marginsDIP.left, displayMetrics)
        val marginRight = Utils.dpToPx(marginsDIP.right, displayMetrics)
        textView.setPadding(marginLeft, textView.paddingTop, marginRight, textView.paddingBottom)
    }

    @BindingAdapter("app:readerMarginVertical")
    @JvmStatic
    fun textViewReaderMarginVertical(textView: TextView, marginsDIP: PreferenceStore.Margins) {
        val displayMetrics = textView.resources.displayMetrics
        val marginTop = Utils.dpToPx(marginsDIP.top, displayMetrics)
        val marginBottom = Utils.dpToPx(marginsDIP.bottom, displayMetrics)
        textView.setPadding(textView.paddingLeft, marginTop, textView.paddingRight, marginBottom)
    }

    @BindingAdapter("app:readerMarginVertical")
    @JvmStatic
    fun scrollViewReaderMarginVertical(scrollView: ScrollView, marginsDIP: PreferenceStore.Margins) {
        val displayMetrics = scrollView.resources.displayMetrics
        val marginTop = Utils.dpToPx(marginsDIP.top, displayMetrics)
        val marginBottom = Utils.dpToPx(marginsDIP.bottom, displayMetrics)
        scrollView.setPadding(scrollView.paddingLeft, marginTop, scrollView.paddingRight, marginBottom)
    }

    @BindingAdapter("app:partProgress")
    @JvmStatic
    fun setScrollReaderPosition(scrollView: ScrollView, position: Double) {
        val childHeight = scrollView.getChildAt(0).height
        val svHeight = scrollView.height
        val scrollPos = (childHeight - svHeight) * position
        if (scrollView.scrollY != scrollPos.toInt()) {
            scrollView.scrollTo(0, scrollPos.toInt())
        }
    }

    @BindingAdapter("app:partProgress")
    @JvmStatic
    fun setPagedReaderPosition(pager: ViewPager2, position: Double) {
        val numPages = pager.adapter?.itemCount ?: 1
        val pagePos = (position * (numPages - 1)).toInt() // Pages are 0-indexed
        pager.setCurrentItem(pagePos, true)
    }
}