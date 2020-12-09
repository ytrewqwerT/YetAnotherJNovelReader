package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.pairtags

import android.text.SpannableStringBuilder
import android.text.style.UnderlineSpan
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.PairTagApplier

/**
 * PairTagApplier for the 'u' html tag.
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to.
 */
class UPairTagApplier(partId: CharSequence) : PairTagApplier(partId) {
    override fun apply(
        args: List<Pair<CharSequence, CharSequence>>, contents: SpannableStringBuilder
    ) {
        warnIfArgsNotEmpty("u", args)
        applySpans(contents, UnderlineSpan())
    }
}