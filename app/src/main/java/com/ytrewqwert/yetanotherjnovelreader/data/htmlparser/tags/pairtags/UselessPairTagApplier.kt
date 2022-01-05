package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.pairtags

import android.text.SpannableStringBuilder
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.PairTagApplier
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.TagApplier

/**
 * PairTagApplier for html tags that don't do anything.
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to. Note that this
 * TagApplier 'handles' (ignores) all arguments.
 * @param[tagString] Refer to [TagApplier.tagString].
 */
class UselessPairTagApplier(partId: CharSequence, override val tagString: CharSequence)
    : PairTagApplier(partId) {

    override fun apply(
        args: List<Pair<CharSequence, CharSequence>>, contents: SpannableStringBuilder
    ) {}
}