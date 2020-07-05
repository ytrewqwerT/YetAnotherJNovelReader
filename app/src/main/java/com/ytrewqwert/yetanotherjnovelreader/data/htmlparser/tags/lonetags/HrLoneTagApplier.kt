package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.lonetags

import android.graphics.Canvas
import android.graphics.Paint
import android.text.SpannableStringBuilder
import android.text.style.ReplacementSpan

/**
 * LoneTagApplier for the 'hr' html tag.
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to.
 */
class HrLoneTagApplier(private val partId: CharSequence) : LoneTagApplier(partId) {
    override fun apply(args: List<Pair<CharSequence, CharSequence>>): SpannableStringBuilder {
        val result = SpannableStringBuilder("-\n")
        applySpans(result, HrSpan(1f))
        return result
    }

    /**
     * Replaces the text with a horizontal rule [thickness] pixels thick.
     *
     * Credit to [this](https://stackoverflow.com/a/43750749) answer by Vishnu M. on S.O.
     */
    private class HrSpan(private val thickness: Float) : ReplacementSpan() {
        override fun getSize(
            paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?
        ): Int = 0

        override fun draw(
            canvas: Canvas, text: CharSequence?, start: Int, end: Int,
            x: Float, top: Int, y: Int, bottom: Int, paint: Paint
        ) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = thickness
            val midY = ((top + bottom) / 2).toFloat()
            canvas.drawLine(0f, midY, canvas.width.toFloat(), midY, paint)
        }

    }
}