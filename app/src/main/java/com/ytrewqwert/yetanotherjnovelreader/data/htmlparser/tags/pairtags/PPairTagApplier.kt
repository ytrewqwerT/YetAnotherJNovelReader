package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.pairtags

import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.style.AlignmentSpan
import android.text.style.LeadingMarginSpan
import android.util.Log

/**
 * PairTagApplier for the 'p' html tag.
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to.
 */
class PPairTagApplier(partId: CharSequence) : PairTagApplier(partId) {
    companion object {
        private const val TAG = "PPairTagApplier"
    }

    override fun apply(args: List<CharSequence>, contents: SpannableStringBuilder) {
        var putLeadingMargin = true
        for (arg in args) {
            when (arg) {
                "class=\"centerp\"" -> {
                    val centerSpan = AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER)
                    applySpans(contents, centerSpan)
                    putLeadingMargin = false
                }
                else -> {
                    Log.w(TAG, "Unhandled arg: $arg")
                    reportUnhandledArg("p", "$arg")
                }
            }
        }
        if (putLeadingMargin) {
            applySpans(contents, LeadingMarginSpan.Standard(100, 0))
        }
        contents.append("\n")
    }
}