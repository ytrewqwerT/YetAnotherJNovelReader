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
     * @param[tagLabel] The tag's label (e.g. 'p' for paragraphs)
     * @param[tagArgs] A list of pairs grouping any arguments with their values.
     */
    fun applyTagPair(
        tagLabel: CharSequence,
        tagArgs: List<Pair<CharSequence, CharSequence>>,
        tagContents: SpannableStringBuilder
    ) {
        PairTagApplier.getApplier(tagLabel, partId).apply(tagArgs, tagContents)
    }

    /**
     * Returns a [SpannableStringBuilder] representing the provided self-closing html tag.
     *
     * @param[tagLabel] The tag's label (e.g. 'br' for a line break)
     * @param[tagArgs] A list of pairs grouping any arguments with their values.
     */
    fun applyLoneTag(
        tagLabel: CharSequence, tagArgs: List<Pair<CharSequence, CharSequence>>
    ): SpannableStringBuilder {
        return LoneTagApplier.getApplier(tagLabel, partId).apply(tagArgs)
    }
}