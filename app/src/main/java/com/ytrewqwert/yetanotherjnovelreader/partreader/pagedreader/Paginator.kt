package com.ytrewqwert.yetanotherjnovelreader.partreader.pagedreader

import android.graphics.text.LineBreaker
import android.text.*
import android.text.style.ImageSpan
import android.text.style.LeadingMarginSpan
import android.util.Log

/**
 * A class used to split a long [Spanned] object into pages.
 *
 * @property[text] The [Spanned] text to split into pages.
 * @property[width] The width of a single page, in px.
 * @property[height] The height of a single page, in px.
 * @property[paint] The paint to be used to draw the text.
 */
class Paginator(
    private val text: Spanned,
    private val width: Int,
    private val height: Int,
    private val paint: TextPaint
) {
    private val pages = ArrayList<CharSequence>()

    init {
        Log.d("Paginator", "width/height = $width/$height")
    }

    /**
     * Splits the given [text] into pages.
     *
     * @return A resulting list of text to be shown in each page.
     */
    fun paginateText(): List<CharSequence> {
        if (width == 0 || height == 0) return emptyList()

        val spans = split(text)
        for (span in spans) {
            var remainingSpan = span.trim()
            do {
                val layout = StaticLayout.Builder.obtain(
                    remainingSpan, 0, remainingSpan.length, paint, width
                )
                    .setHyphenationFrequency(StaticLayout.HYPHENATION_FREQUENCY_FULL)
                    .setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD)
                    .build()

                var lineNum = 0
                while (
                    lineNum < layout.lineCount && layout.getLineBottom(lineNum) < height
                ) lineNum++
                // Ensure at least one "line" is displayed.
                // This is more a stop-gap measure to allow images that cannot fit on the page
                // to still be at least partially displayed and not cause an infinite loop.
                if (lineNum == 0) lineNum = 1

                addPage(remainingSpan.subSequence(0, layout.getLineStart(lineNum)))

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

    private fun addPage(text: CharSequence) {
        // Ignore leading/trailing whitespace
        if (text.isNotBlank()) pages.add(text.trim())
    }

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