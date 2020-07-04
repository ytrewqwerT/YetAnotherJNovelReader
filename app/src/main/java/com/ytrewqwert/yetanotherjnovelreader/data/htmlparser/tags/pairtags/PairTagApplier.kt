package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.pairtags

import android.text.SpannableStringBuilder
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.TagApplier

/**
 * *Interface* for the application of open/close html tag pairs.
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to.
 */
abstract class PairTagApplier(partId: CharSequence) : TagApplier(partId) {
    /** Applies the html tag pair modified by properties specified by [args] to [contents]. */
    abstract fun apply(args: List<Pair<CharSequence, CharSequence>>, contents: SpannableStringBuilder)

    companion object {
        /** Returns the [PairTagApplier] for [tag], injecting [partId] as its source part. */
        fun getApplier(tag: CharSequence, partId: CharSequence): PairTagApplier = when(tag) {
            "h1", "h2", "h3", "h4", "h5", "h6" -> HPairTagApplier(partId, tag[1] - '0')
            "p" -> PPairTagApplier(partId)
            "b" -> BPairTagApplier(partId)
            "em" -> EmPairTagApplier(partId)
            else -> DummyPairTagApplier(partId, tag)
        }
    }
}