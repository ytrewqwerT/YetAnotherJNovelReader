package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.lonetags

import android.text.Html
import android.text.SpannableStringBuilder

/**
 * LoneTagApplier for the 'img' html tag.
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to.
 */
class ImgLoneTagApplier(partId: CharSequence) : LoneTagApplier(partId) {
    override fun apply(args: List<CharSequence>): SpannableStringBuilder {
        // Use the built-in parser to inject a placeholder image.
        val imgSpan = Html.fromHtml("<img ${args.joinToString(" ")} />", 0)
        return SpannableStringBuilder(imgSpan).apply {
            // Add extra spacing between image and surrounding content
            insert(0, "\n")
            append("\n\n")
        }
    }
}