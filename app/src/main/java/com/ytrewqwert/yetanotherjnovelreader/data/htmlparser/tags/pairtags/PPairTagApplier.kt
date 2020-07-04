package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.pairtags

import android.text.SpannableStringBuilder
import android.text.style.LeadingMarginSpan
import android.util.Log
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.TagArgApplier

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
        applySpans(contents, LeadingMarginSpan.Standard(100, 0)) // Paragraph indentation
        for (arg in args) {
            val (type, value) = arg
            val applier = TagArgApplier.getApplier(type)
            if (applier?.applyArg(value, contents) != true) {
                Log.w(TAG, "Unhandled arg: $arg")
                reportUnhandledArg("p", "$type=$value")
                continue
            }
        }
        contents.append("\n")
    }
}