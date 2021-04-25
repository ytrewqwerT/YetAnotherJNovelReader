package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.pairtags

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.TypefaceSpan
import com.ytrewqwert.yetanotherjnovelreader.App
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.PairTagApplier
import com.ytrewqwert.yetanotherjnovelreader.data.local.preferences.FontResIds

/**
 * PairTagApplier for the 'code' html tag.
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to.
 */
class CodePairTagApplier(partId: CharSequence) : PairTagApplier(partId) {
    override val tagString: CharSequence = "code"

    override fun apply(
        args: List<Pair<CharSequence, CharSequence>>, contents: SpannableStringBuilder
    ) {
        warnIfArgsNotEmpty(args)
        // Ideally would let the user customise the monospace font similarly to the normal font.
        val font = App.getFont(FontResIds.CUTIVE_MONO)
        applySpans(contents, TypefaceSpan(font))
    }
}