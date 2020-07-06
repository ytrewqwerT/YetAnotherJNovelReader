package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.pairtags

import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.PairTagApplier

/**
 * PairTagApplier for the 'sup' html tag.
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to.
 */
class SupPairTagApplier(partId: CharSequence) : PairTagApplier(partId) {
    override fun apply(
        args: List<Pair<CharSequence, CharSequence>>,
        contents: SpannableStringBuilder
    ) {
        warnIfArgsNotEmpty("b", args)
        applySpans(contents, SuperscriptSpan(), RelativeSizeSpan(0.7f))
    }
}