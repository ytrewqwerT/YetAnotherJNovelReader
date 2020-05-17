package com.ytrewqwert.yetanotherjnovelreader.partreader.pagedreader

import android.text.*
import android.text.style.ImageSpan
import android.text.style.LeadingMarginSpan
import android.util.Log

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
                    .setJustificationMode(StaticLayout.JUSTIFICATION_MODE_INTER_WORD)
                    .build()

                var lineNum = 0
                while (
                    lineNum < layout.lineCount && layout.getLineBottom(lineNum) < height
                ) lineNum++

                addPage(remainingSpan.subSequence(0, layout.getLineStart(lineNum)))

                while (lineNum < layout.lineCount && lineIsBlank(layout, lineNum)) lineNum++
                val nextStart = layout.getLineStart(lineNum)
                val overflowed = paragraphOverflowed(remainingSpan, nextStart)
                remainingSpan = SpannableString(
                    remainingSpan.subSequence(nextStart, remainingSpan.length)
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
        if (text.isNotBlank()) pages.add(text)
    }

    private fun lineIsBlank(layout: StaticLayout, lineNo: Int): Boolean {
        val start = layout.getLineStart(lineNo)
        val end = layout.getLineEnd(lineNo)
        val line = layout.text.subSequence(start, end)
        return line.isBlank()
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