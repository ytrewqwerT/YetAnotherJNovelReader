package com.ytrewqwert.yetanotherjnovelreader.partreader.pagedreader

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.text.style.LeadingMarginSpan
import android.util.Log
import android.widget.TextView

/**
 * A singleton used to split a long [Spanned] object into pages.
 */
object Paginator {
    /**
     * Splits some text into pages.
     *
     * @param[textView] The TextView that will contain each page.
     * @param[text] The text to be paginated.
     * @param[pageHeight] The height of each page.
     *
     * @return A resulting list of text to be shown in each page.
     */
    fun paginate(textView: TextView?, text: Spanned, pageHeight: Int): List<CharSequence> {
        if (textView == null) return emptyList()
        if (textView.width <= 0 || pageHeight <= 0) return emptyList()

        Log.d("Paginator", "h/w = ${pageHeight}/${textView.width}")

        val pages = ArrayList<CharSequence>()

        val spans = split(text)
        for (span in spans) {
            var remainingSpan = span.trim()
            do {
                textView.text = remainingSpan
                val layout = textView.layout

                var lineNum = 0
                while (
                    lineNum < layout.lineCount && layout.getLineBottom(lineNum) < pageHeight
                ) lineNum++
                // Ensure at least one "line" is displayed.
                // This is more a stop-gap measure to allow images that cannot fit on the page
                // to still be at least partially displayed and not cause an infinite loop.
                if (lineNum == 0) lineNum = 1

                val pageText = remainingSpan.subSequence(0, layout.getLineStart(lineNum))
                if (pageText.isNotBlank()) pages.add(pageText.trim())

                val nextStart = layout.getLineStart(lineNum)
                val overflowed = paragraphOverflowed(remainingSpan, nextStart)
                remainingSpan = SpannableString(
                    remainingSpan.subSequence(nextStart, remainingSpan.length).trim()
                )
                if (overflowed) {
                    val paragraphEnd = remainingSpan.indexOf('\n')
                    val leadingMarginSpan = remainingSpan.getSpans(
                        0, paragraphEnd, LeadingMarginSpan::class.java
                    ).firstOrNull()
                    if (leadingMarginSpan != null) remainingSpan.removeSpan(leadingMarginSpan)
                }
            } while (remainingSpan.isNotEmpty())
        }
        return pages
    }

    // Checks if the line of text starting from the given index continues a paragraph from the
    // previous page
    private fun paragraphOverflowed(text: CharSequence, lineStartPos: Int): Boolean {
        if (lineStartPos == 0) return false
        return text[lineStartPos - 1] != '\n'
    }

    // Separates images from the text
    // Returns the split Spanned objects in the order they occurred in the original Spanned
    private fun split(spanned: Spanned): List<Spanned> {
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