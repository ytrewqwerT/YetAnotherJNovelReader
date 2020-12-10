package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.pairtags

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.LeadingMarginSpan
import android.text.style.RelativeSizeSpan
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.PairTagApplier
import com.ytrewqwert.yetanotherjnovelreader.data.local.preferences.PrefDefaults

/**
 * PairTagApplier for the 'p' html tag.
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to.
 */
class PPairTagApplier(partId: CharSequence) : PairTagApplier(partId) {
    override val tagString: CharSequence = "p"

    override fun apply(
        args: List<Pair<CharSequence, CharSequence>>, contents: SpannableStringBuilder
    ) {
        val readerSpanSettings = Repository.getInstance()?.getReaderSettings()

        val indentation = readerSpanSettings?.paraIndent ?: PrefDefaults.PARA_INDENT
        applySpans(contents, LeadingMarginSpan.Standard(indentation, 0)) // Paragraph indentation
        applyArgs(contents, args)
        contents.append("\n\n") // Extra '\n' for applying paragraph spacing

        val paragraphSpacing = (readerSpanSettings?.paraSpacing ?: PrefDefaults.PARA_SPACING) - 1
        contents.setSpan(
            RelativeSizeSpan(paragraphSpacing),
            contents.length-1,
            contents.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}