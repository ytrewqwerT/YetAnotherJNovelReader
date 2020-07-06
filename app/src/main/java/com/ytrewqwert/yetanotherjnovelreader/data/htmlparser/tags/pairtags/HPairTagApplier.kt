package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.pairtags

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.PairTagApplier

/**
 * PairTagApplier for header html tags. (i.e. h1 - h6).
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to.
 * @param[headerSize] The header value (i.e. 1 for h1, 2 for h2, etc.)
 */
class HPairTagApplier(partId: CharSequence, private val headerSize: Int) : PairTagApplier(partId) {
    companion object {
        private val HEADING_SIZES = arrayOf(1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1f)
    }

    override fun apply(
        args: List<Pair<CharSequence, CharSequence>>, contents: SpannableStringBuilder
    ) {
        warnIfArgsNotEmpty("h$headerSize", args)
        applySpans(
            contents,
            RelativeSizeSpan(HEADING_SIZES[headerSize - 1]),
            StyleSpan(Typeface.BOLD)
        )
        contents.insert(0, "\n")
        contents.append("\n\n")
    }
}