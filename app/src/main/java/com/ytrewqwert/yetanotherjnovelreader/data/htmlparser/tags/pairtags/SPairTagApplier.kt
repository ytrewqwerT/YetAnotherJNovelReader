package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.pairtags

import android.text.SpannableStringBuilder
import android.text.style.StrikethroughSpan
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.PairTagApplier

/**
 * PairTagApplier for the 's' html tag.
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to.
 */
class SPairTagApplier(partId: CharSequence) : PairTagApplier(partId) {
    override fun apply(
        args: List<Pair<CharSequence, CharSequence>>, contents: SpannableStringBuilder
    ) {
        warnIfArgsNotEmpty("em", args)
        applySpans(contents, StrikethroughSpan())
    }
}