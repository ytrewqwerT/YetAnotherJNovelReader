package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.lonetags

import android.text.SpannableStringBuilder
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.LoneTagApplier

/**
 * LoneTagApplier for the 'br' html tag.
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to.
 */
class BrLoneTagApplier(partId: CharSequence) : LoneTagApplier(partId) {
    override val tagString: CharSequence = "br"

    override fun apply(args: List<Pair<CharSequence, CharSequence>>): SpannableStringBuilder {
        warnIfArgsNotEmpty(args)
        return SpannableStringBuilder("\n")
    }
}