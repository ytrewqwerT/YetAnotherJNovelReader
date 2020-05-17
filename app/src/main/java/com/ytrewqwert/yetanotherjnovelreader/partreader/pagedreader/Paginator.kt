package com.ytrewqwert.yetanotherjnovelreader.partreader.pagedreader

import android.text.SpannableString
import android.text.Spanned
import android.text.StaticLayout
import android.text.TextPaint
import android.text.style.ImageSpan
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
            val layout = StaticLayout.Builder.obtain(span, 0, span.length, paint, width)
                .setHyphenationFrequency(StaticLayout.HYPHENATION_FREQUENCY_FULL)
                .setJustificationMode(StaticLayout.JUSTIFICATION_MODE_INTER_WORD)
                .build()
            var adjustedHeight = height
            var curLine = 0
            while (curLine < layout.lineCount && lineIsBlank(layout, curLine)) curLine++
            var nextChar = layout.getLineStart(curLine)
            while (curLine < layout.lineCount) {
                if (adjustedHeight < layout.getLineBottom(curLine)) {
                    addPage(span.subSequence(nextChar, layout.getLineStart(curLine)))
                    while (curLine < layout.lineCount && lineIsBlank(layout, curLine)) curLine++
                    nextChar = layout.getLineStart(curLine)
                    adjustedHeight = height + layout.getLineTop(curLine)
                }
                curLine++
            }
            addPage(span.subSequence(nextChar, span.length))
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

    // Separates images from the text
    // Returns the split Spanned objects in the order they occurred in the original Spanned
    private fun split(spanned: Spanned): List<Spanned> {
        val resultList = ArrayList<Spanned>()
        var textSpanStart = 0
        var textSpanEnd = 0
        while (textSpanEnd < spanned.length) {
            textSpanEnd = spanned.nextSpanTransition(textSpanStart, spanned.length, ImageSpan::class.java)
            resultList.add(
                SpannableString(
                    spanned.subSequence(
                        textSpanStart,
                        textSpanEnd
                    )
                )
            )
            textSpanStart = textSpanEnd
        }
        return resultList
    }
}