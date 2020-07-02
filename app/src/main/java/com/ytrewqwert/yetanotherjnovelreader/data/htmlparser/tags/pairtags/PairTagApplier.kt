package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.pairtags

import android.text.Spannable
import android.text.SpannableStringBuilder
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.TagApplier
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.lonetags.BrLoneTagApplier
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.lonetags.DummyLoneTagApplier
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.lonetags.ImgLoneTagApplier
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.lonetags.LoneTagApplier

/**
 * *Interface* for the application of open/close html tag pairs.
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to.
 */
abstract class PairTagApplier(partId: CharSequence) : TagApplier(partId) {
    /** Applies the html tag pair modified by properties specified by [args] to [contents]. */
    abstract fun apply(args: List<CharSequence>, contents: SpannableStringBuilder)

    /** Applies [spans], exclusive-exclusive, to the entire length of [target]. */
    protected fun applySpans(target: Spannable, vararg spans: Any) {
        for (span in spans) {
            target.setSpan(span, 0, target.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    companion object {
        /** Returns the [LoneTagApplier] for [tag], injecting [partId] as its source part. */
        fun getApplier(tag: CharSequence, partId: CharSequence): PairTagApplier = when(tag) {
            "h1", "h2", "h3", "h4", "h5", "h6" -> HPairTagApplier(partId, tag[1] - '0')
            "p" -> PPairTagApplier(partId)
            "b" -> BPairTagApplier(partId)
            "em" -> EmPairTagApplier(partId)
            else -> DummyPairTagApplier(partId, tag)
        }
    }
}