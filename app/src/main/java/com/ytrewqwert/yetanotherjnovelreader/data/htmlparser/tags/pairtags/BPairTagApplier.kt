package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.pairtags

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.PairTagApplier

/**
 * PairTagApplier for the 'b' html tag.
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to.
 */
class BPairTagApplier(partId: CharSequence) : PairTagApplier(partId) {
    override val tagString: CharSequence = "b"

    override fun apply(
        args: List<Pair<CharSequence, CharSequence>>, contents: SpannableStringBuilder
    ) {
        warnIfArgsNotEmpty(args)
        applySpans(contents, StyleSpan(Typeface.BOLD))
    }
}