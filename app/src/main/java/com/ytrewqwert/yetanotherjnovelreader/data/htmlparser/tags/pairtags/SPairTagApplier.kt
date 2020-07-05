package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.pairtags

import android.text.SpannableStringBuilder
import android.text.style.StrikethroughSpan

class SPairTagApplier(partId: CharSequence) : PairTagApplier(partId) {
    override fun apply(
        args: List<Pair<CharSequence, CharSequence>>, contents: SpannableStringBuilder
    ) {
        warnIfArgsNotEmpty("em", args)
        applySpans(contents, StrikethroughSpan())
    }
}