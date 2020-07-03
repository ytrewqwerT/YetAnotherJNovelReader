package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.lonetags

import android.text.SpannableStringBuilder

/**
 * LoneTagApplier for the 'br' html tag.
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to.
 */
class BrLoneTagApplier(partId: CharSequence) : LoneTagApplier(partId) {
    override fun apply(args: List<Pair<CharSequence, CharSequence>>): SpannableStringBuilder {
        warnIfArgsNotEmpty("br", args)
        return SpannableStringBuilder("\n")
    }
}