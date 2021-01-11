package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.lonetags

import android.text.Html
import android.text.SpannableStringBuilder
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.LoneTagApplier
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.tagargs.classappliers.CenterpApplier

/**
 * LoneTagApplier for the 'img' html tag.
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to.
 */
class ImgLoneTagApplier(partId: CharSequence) : LoneTagApplier(partId) {
    override val tagString: CharSequence = "img"

    override fun apply(args: List<Pair<CharSequence, CharSequence>>): SpannableStringBuilder {
        // Use the built-in parser to inject a placeholder image.
        val argsCombinedTypeValue = args.map { "${it.first}=${it.second}" }
        val imgSpan = Html.fromHtml("<img ${argsCombinedTypeValue.joinToString(" ")} />", 0)
        return SpannableStringBuilder(imgSpan).apply {
            // Add extra spacing between image and surrounding content, and left/right center.
            CenterpApplier.applyArg("centerp", this)
            insert(0, "\n")
            append("\n\n")
        }
    }
}