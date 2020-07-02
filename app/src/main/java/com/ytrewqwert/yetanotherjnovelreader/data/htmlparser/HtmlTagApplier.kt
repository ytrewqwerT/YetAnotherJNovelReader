package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser

import android.text.SpannableStringBuilder
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.lonetags.LoneTagApplier
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.pairtags.PairTagApplier

/**
 * Class for processing html tags.
 *
 * @param[partId] The source part to attribute any unhandled tags to when logging.
 */
class HtmlTagApplier(private val partId: String) {
    /**
     * Formats [tagContents] based on an open/close html tag pair.
     *
     * @param[tagLabelTokens] A list containing the tag's label followed by any provided arguments.
     */
    fun applyTagPair(
        tagLabelTokens: List<CharSequence>, tagContents: SpannableStringBuilder
    ) {
        val label = tagLabelTokens[0]
        val args = tagLabelTokens.subList(1, tagLabelTokens.size)
        PairTagApplier.getApplier(label, partId).apply(args, tagContents)
    }

    /**
     * Returns a [SpannableStringBuilder] representing the provided self-closing html tag.
     *
     * @param[tagLabelTokens] A list containing the tag's label followed by any provided arguments.
     */
    fun applyLoneTag(
        tagLabelTokens: List<CharSequence>
    ): SpannableStringBuilder {
        val label = tagLabelTokens[0]
        val args = tagLabelTokens.subList(1, tagLabelTokens.size)
        return LoneTagApplier.getApplier(label, partId).apply(args)
    }
}