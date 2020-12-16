package com.ytrewqwert.yetanotherjnovelreader

import android.util.TypedValue
import android.widget.ScrollView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ytrewqwert.yetanotherjnovelreader.data.local.preferences.ReaderPreferenceStore
import kotlin.math.roundToInt

/** Custom databinding binding adapters used by the app. */
object BindingAdapters {
    /** Sets the size of text in a [TextView]. */
    @BindingAdapter("android:textSize")
    @JvmStatic
    fun textViewTextSize(textView: TextView, sizeSP: Int) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSP.toFloat())
    }

    /** Sets the size of the horizontal margins around a [TextView]. */
    @BindingAdapter("readerMarginHorizontal")
    @JvmStatic
    fun textViewReaderMarginHorizontal(textView: TextView, marginsDP: ReaderPreferenceStore.Margins) {
        val displayMetrics = textView.resources.displayMetrics
        val marginLeft = Utils.dpToPx(marginsDP.left, displayMetrics)
        val marginRight = Utils.dpToPx(marginsDP.right, displayMetrics)
        textView.setPadding(marginLeft, textView.paddingTop, marginRight, textView.paddingBottom)
    }

    /** Sets the size of the vertical margins around a [TextView]. */
    @BindingAdapter("readerMarginVertical")
    @JvmStatic
    fun textViewReaderMarginVertical(textView: TextView, marginsDP: ReaderPreferenceStore.Margins) {
        val displayMetrics = textView.resources.displayMetrics
        val marginTop = Utils.dpToPx(marginsDP.top, displayMetrics)
        val marginBottom = Utils.dpToPx(marginsDP.bottom, displayMetrics)
        textView.setPadding(textView.paddingLeft, marginTop, textView.paddingRight, marginBottom)
    }

    /** Sets the size of the vertical margins around a [ScrollView]. */
    @BindingAdapter("readerMarginVertical")
    @JvmStatic
    fun scrollViewReaderMarginVertical(scrollView: ScrollView, marginsDP: ReaderPreferenceStore.Margins) {
        val displayMetrics = scrollView.resources.displayMetrics
        val marginTop = Utils.dpToPx(marginsDP.top, displayMetrics)
        val marginBottom = Utils.dpToPx(marginsDP.bottom, displayMetrics)
        scrollView.setPadding(scrollView.paddingLeft, marginTop, scrollView.paddingRight, marginBottom)
    }

    /** Sets a [ScrollView]'s scroll position to the given value (between 0 and 1). */
    @BindingAdapter("partProgress")
    @JvmStatic
    fun setScrollReaderPosition(scrollView: ScrollView, position: Double) {
        val childHeight = scrollView.getChildAt(0).height
        val svHeight = scrollView.height
        val scrollPos = (childHeight - svHeight) * position
        scrollView.scrollTo(0, scrollPos.toInt())
    }

    /**
     * Sets a [ViewPager2]'s currently shown page to be at the page most closely corresponding to
     * given value as a proportion. e.g. A [position] of 0.5 would move the ViewPager2 to roughly
     * the middle page, w.r.t. the pager's total number of pages.
     */
    @BindingAdapter("partProgress")
    @JvmStatic
    fun setPagedReaderPosition(pager: ViewPager2, position: Double) {
        val numPages = pager.adapter?.itemCount ?: 1
        val pagePos = (position * (numPages - 1)).roundToInt() // Pages are 0-indexed
        pager.setCurrentItem(pagePos, true)
    }
}