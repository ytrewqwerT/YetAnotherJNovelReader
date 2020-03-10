package com.ytrewqwert.yetanotherjnovelreader.partreader.pagedreader

import android.text.SpannableString
import android.text.Spanned
import android.text.StaticLayout
import android.text.TextPaint
import android.text.style.ImageSpan
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagedReaderAdapter(
    fa: FragmentActivity
) : FragmentStateAdapter(fa) {

    private var numPages = 0

    override fun getItemCount(): Int = numPages

    override fun createFragment(position: Int): Fragment {
        TODO("Not yet implemented")
    }

    fun setReaderContents(content: CharSequence) {

    }

    class Paginator(
        private val text: Spanned,
        private val width: Int,
        private val paint: TextPaint
    ) {
        private val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .build()

        fun paginate() {

        }

        // Separates images from the text
        // Returns the split Spanned objects in the order they occurred in the original Spanned
        fun split(spanned: Spanned): List<Spanned> {
            val resultList = ArrayList<Spanned>()
            var textSpanStart = 0
            var textSpanEnd = 0
            while (textSpanEnd < spanned.length) {
                textSpanEnd = spanned.nextSpanTransition(textSpanStart, spanned.length, ImageSpan::class.java)
                resultList.add(SpannableString(spanned.subSequence(textSpanStart, textSpanEnd)))
                textSpanStart = textSpanEnd
            }
            return resultList
        }
    }
}