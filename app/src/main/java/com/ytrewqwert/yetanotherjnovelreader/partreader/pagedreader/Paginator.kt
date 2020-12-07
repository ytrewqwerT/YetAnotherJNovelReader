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
                var nextPageStart = getPageEndIndex(textView, remainingSpan, pageHeight)
                var pageText = remainingSpan.subSequence(0, nextPageStart).trim()
                // Due to how justified text alignment works, the first trim may result in the last
                // word in the page to be pushed into its own line, so trim the page again, this
                // time using only the currently predicted page's text.
                nextPageStart = getPageEndIndex(textView, pageText, pageHeight)
                pageText = remainingSpan.subSequence(0, nextPageStart).trim()

                if (pageText.isNotBlank()) pages.add(pageText)

                val overflowed = paragraphOverflowed(remainingSpan, nextPageStart)
                remainingSpan = SpannableString(
                    remainingSpan.subSequence(nextPageStart, remainingSpan.length).trim()
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

    // Returns the index of the character after the last character that can fit in a page. If
    // splitting at that point would result in a word being split across 2 pages, then the index
    // after the end of the previous word is returned instead.
    private fun getPageEndIndex(textView: TextView, text: CharSequence, pageHeight: Int): Int {
        textView.text = text
        val layout = textView.layout

        var lineNum = 0
        while (
            lineNum < layout.lineCount && layout.getLineBottom(lineNum) < pageHeight
        ) lineNum++
        // Ensure at least one "line" is displayed.
        // This is more a stop-gap measure to allow images that cannot fit on the page
        // to still be at least partially displayed and not cause an infinite loop.
        if (lineNum == 0) lineNum = 1

        // Get the index of the end-of-page char and backtrack if necessary to prevent words being
        // split across pages.
        var endIndex = layout.getLineStart(lineNum)
        while (
            endIndex in (1 until text.length)
            && !text[endIndex].isWhitespace()
        ) endIndex--
        return endIndex
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