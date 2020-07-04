package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.tagargs.classappliers

import android.text.SpannableStringBuilder
import android.text.style.LeadingMarginSpan
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.TagArgApplier

/** [TagArgApplier] for the 'class' argument with value 'noindent'. */
object NoindentApplier : TagArgApplier {
    override fun applyArg(value: CharSequence, contents: SpannableStringBuilder): Boolean {
        val leadingMarginSpan = contents.getSpans(
            0, contents.length, LeadingMarginSpan::class.java
        ).firstOrNull()
        if (leadingMarginSpan != null) contents.removeSpan(leadingMarginSpan)
        return true
    }
}