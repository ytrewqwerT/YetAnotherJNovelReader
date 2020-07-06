package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags

import android.text.SpannableStringBuilder
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.tagargs.ClassTagArgApplier

/** *Interface* for the application of arguments provided to html tags. */
interface TagArgApplier {
    /**
     * Applies the html tag argument's [value] to [contents].
     *
     * @return true if the arg was successfully applied and false otherwise.
     */
    fun applyArg(value: CharSequence, contents: SpannableStringBuilder): Boolean

    companion object {
        /** Returns the [TagArgApplier] for [arg], or null if no such applier exists. */
        fun getApplier(arg: CharSequence): TagArgApplier? = when(arg) {
            "class" -> ClassTagArgApplier
            else -> null
        }
    }
}