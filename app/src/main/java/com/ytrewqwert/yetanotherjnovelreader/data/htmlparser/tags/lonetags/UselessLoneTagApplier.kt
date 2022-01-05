package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.lonetags

import android.text.SpannableStringBuilder
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.LoneTagApplier
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.TagApplier

/**
 * lONETagApplier for html tags that don't do anything.
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to. Note that this
 * TagApplier 'handles' (ignores) all arguments.
 * @param[tagString] Refer to [TagApplier.tagString].
 */
class UselessLoneTagApplier(partId: CharSequence, override val tagString: CharSequence)
    : LoneTagApplier(partId) {

    override fun apply(args: List<Pair<CharSequence, CharSequence>>): SpannableStringBuilder {
        return SpannableStringBuilder("")
    }
}