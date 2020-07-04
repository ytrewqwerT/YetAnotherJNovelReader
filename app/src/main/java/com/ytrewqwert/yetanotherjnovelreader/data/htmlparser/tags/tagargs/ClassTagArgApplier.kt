package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.tagargs

import android.text.SpannableStringBuilder
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.TagArgApplier
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.tagargs.classappliers.*

/** [TagArgApplier] for the 'class' argument. */
object ClassTagArgApplier : TagArgApplier {
    override fun applyArg(value: CharSequence, contents: SpannableStringBuilder): Boolean {
        val classes = value.trim(' ', '"').split(' ')
        for (cls in classes) {
            val applier = getClassApplier(cls) ?: return false
            if (!applier.applyArg(cls, contents)) return false
        }
        return true
    }

    /** Returns the TagArgApplier for having class [cls]. */
    private fun getClassApplier(cls: CharSequence): TagArgApplier? = when(cls) {
        "centerp" -> CenterpApplier
        "noindent" -> NoindentApplier
        else -> null
    }
}