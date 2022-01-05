package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.pairtags

import android.text.SpannableStringBuilder
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.PairTagApplier

/**
 * PairTagApplier for the 'head' html tag.
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to.
 */
class HeadPairTagApplier(partId: CharSequence) : PairTagApplier(partId) {
    override val tagString: CharSequence = "head"

    override fun apply(
        args: List<Pair<CharSequence, CharSequence>>, contents: SpannableStringBuilder
    ) {
        warnIfArgsNotEmpty(args)
        // Only care about the body. Remove the head.
        contents.clear()
    }
}