package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.pairtags

import android.text.SpannableStringBuilder
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.PairTagApplier

/**
 * PairTagApplier for the 'li' html tag.
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to.
 */
class LiPairTagApplier(private val partId: CharSequence) : PairTagApplier(partId) {
    override val tagString: CharSequence = "li"

    override fun apply(
        args: List<Pair<CharSequence, CharSequence>>, contents: SpannableStringBuilder
    ) {
        warnIfArgsNotEmpty(args)

        // Assume the <li> is in a <ul> since <ol> and <menu> aren't handled.
        contents.insert(0, "\u2022 ") // Bullet point
        contents.append("\n")
    }
}