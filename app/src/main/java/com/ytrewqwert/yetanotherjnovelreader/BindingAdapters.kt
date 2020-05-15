package com.ytrewqwert.yetanotherjnovelreader

import android.util.TypedValue
import android.widget.ScrollView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ytrewqwert.yetanotherjnovelreader.data.local.preferences.PreferenceStore
import kotlin.math.roundToInt

object BindingAdapters {

    @BindingAdapter("android:textSize")
    @JvmStatic
    fun textViewTextSize(textView: TextView, sizeSP: Int) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSP.toFloat())
    }

    @BindingAdapter("readerMarginHorizontal")
    @JvmStatic
    fun textViewReaderMarginHorizontal(textView: TextView, marginsDP: PreferenceStore.Margins) {
        val displayMetrics = textView.resources.displayMetrics
        val marginLeft = Utils.dpToPx(marginsDP.left, displayMetrics)
        val marginRight = Utils.dpToPx(marginsDP.right, displayMetrics)
        textView.setPadding(marginLeft, textView.paddingTop, marginRight, textView.paddingBottom)
    }

    @BindingAdapter("readerMarginVertical")
    @JvmStatic
    fun textViewReaderMarginVertical(textView: TextView, marginsDP: PreferenceStore.Margins) {
        val displayMetrics = textView.resources.displayMetrics
        val marginTop = Utils.dpToPx(marginsDP.top, displayMetrics)
        val marginBottom = Utils.dpToPx(marginsDP.bottom, displayMetrics)
        textView.setPadding(textView.paddingLeft, marginTop, textView.paddingRight, marginBottom)
    }

    @BindingAdapter("readerMarginVertical")
    @JvmStatic
    fun scrollViewReaderMarginVertical(scrollView: ScrollView, marginsDP: PreferenceStore.Margins) {
        val displayMetrics = scrollView.resources.displayMetrics
        val marginTop = Utils.dpToPx(marginsDP.top, displayMetrics)
        val marginBottom = Utils.dpToPx(marginsDP.bottom, displayMetrics)
        scrollView.setPadding(scrollView.paddingLeft, marginTop, scrollView.paddingRight, marginBottom)
    }

    @BindingAdapter("partProgress")
    @JvmStatic
    fun setScrollReaderPosition(scrollView: ScrollView, position: Double) {
        val childHeight = scrollView.getChildAt(0).height
        val svHeight = scrollView.height
        val scrollPos = (childHeight - svHeight) * position
        scrollView.scrollTo(0, scrollPos.toInt())
    }

    @BindingAdapter("partProgress")
    @JvmStatic
    fun setPagedReaderPosition(pager: ViewPager2, position: Double) {
        val numPages = pager.adapter?.itemCount ?: 1
        val pagePos = (position * (numPages - 1)).roundToInt() // Pages are 0-indexed
        pager.setCurrentItem(pagePos, true)
    }
}