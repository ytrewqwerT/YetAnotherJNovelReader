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

    override fun apply(args: List<Pair<CharSequence, CharSequence>>, contents: SpannableStringBuilder) {
        var putLeadingMargin = true
        for (arg in args) {
            val (type, value) = arg
            when (type) {
                "class" -> {
                    val splitValues = value.trim('"').split(' ')
                    for (v in splitValues) when(v) {
                        "centerp" -> {
                            val centerSpan = AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER)
                            applySpans(contents, centerSpan)
                            putLeadingMargin = false
                        }
                        else -> {
                            Log.w(TAG, "Unhandled arg: $arg")
                            reportUnhandledArg("p", "$type=\"$v\"")
                        }
                    }
                }
                else -> {
                    Log.w(TAG, "Unhandled arg: $arg")
                    reportUnhandledArg("p", "$type=$value")
                }
            }
        }
        if (putLeadingMargin) {
            applySpans(contents, LeadingMarginSpan.Standard(100, 0))
        }
        contents.append("\n")
    }
}