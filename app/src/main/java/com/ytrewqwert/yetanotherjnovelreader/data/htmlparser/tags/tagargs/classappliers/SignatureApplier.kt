package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.tagargs.classappliers

import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.style.AlignmentSpan
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.TagApplier
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.TagArgApplier

/** [TagArgApplier] for the 'class' argument with value 'signature'. */
object SignatureApplier : TagArgApplier {
    override fun applyArg(value: CharSequence, contents: SpannableStringBuilder): Boolean {
        NoindentApplier.applyArg("noindent", contents)
        TagApplier.applySpans(contents, AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE))
        return true
    }
}