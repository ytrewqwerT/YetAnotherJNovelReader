package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.pairtags

import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.PairTagApplier

/**
 * PairTagApplier for the 'ul' html tag.
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to.
 */
class UlPairTagApplier(partId: CharSequence) : PairTagApplier(partId) {
    override val tagString: CharSequence = "ul"

    override fun apply(
        args: List<Pair<CharSequence, CharSequence>>, contents: SpannableStringBuilder
    ) {
        warnIfArgsNotEmpty(args)
        // <li> currently assumes <ul> (<ol> and <menu> aren't handled).
        // Just add extra spacing above and below the list to separate from surrounding paragraphs.
        val lineSpacing = SpannableStringBuilder("\n")
        applySpans(lineSpacing, RelativeSizeSpan(0.5f))
        contents.insert(0, lineSpacing)
        contents.append(lineSpacing)
    }
}