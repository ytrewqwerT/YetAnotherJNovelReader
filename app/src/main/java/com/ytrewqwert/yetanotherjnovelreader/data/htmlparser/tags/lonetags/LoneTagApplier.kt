package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.lonetags

import android.text.SpannableStringBuilder
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.TagApplier

/**
 * *Interface* for the application of self-closing html tags.
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to.
 */
abstract class LoneTagApplier(partId: CharSequence) : TagApplier(partId) {
    /** Returns a  representation of the html tag with properties specified by [args]. */
    abstract fun apply(args: List<Pair<CharSequence, CharSequence>>): SpannableStringBuilder

    companion object {
        /** Returns the [LoneTagApplier] for [tag], injecting [partId] for its source part. */
        fun getApplier(tag: CharSequence, partId: CharSequence): LoneTagApplier = when(tag) {
            "br" -> BrLoneTagApplier(partId)
            "hr" -> HrLoneTagApplier(partId)
            "img" -> ImgLoneTagApplier(partId)
            else -> DummyLoneTagApplier(partId, tag)
        }
    }
}