package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.pairtags

import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.SubscriptSpan
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.PairTagApplier

/**
 * PairTagApplier for the 'sub' html tag.
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to.
 */
class SubPairTagApplier(partId: CharSequence) : PairTagApplier(partId) {
    override fun apply(
        args: List<Pair<CharSequence, CharSequence>>,
        contents: SpannableStringBuilder
    ) {
        warnIfArgsNotEmpty("sub", args)
        applySpans(contents, RelativeSizeSpan(0.7f), SubscriptSpan())
    }
}