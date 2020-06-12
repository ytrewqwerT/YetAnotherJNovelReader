package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser

import android.graphics.Typeface
import android.text.Html
import android.text.Layout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AlignmentSpan
import android.text.style.LeadingMarginSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import com.ytrewqwert.yetanotherjnovelreader.data.firebase.FirestoreDataInterface

/** Class for processing html tags. */
class HtmlTagApplier(private val partId: String) {
    companion object {
        private const val TAG = "HtmlTagApplier"
        private val HEADING_SIZES = arrayOf(1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1f)
    }

    /**
     * Formats [tagContents] based on an open/close html tag pair.
     *
     * @param[tagLabelTokens] A list containing the tag's label followed by any provided arguments.
     */
    fun applyTagPair(
        tagLabelTokens: List<CharSequence>, tagContents: SpannableStringBuilder
    ) {
        val label = tagLabelTokens[0]
        val args = tagLabelTokens.subList(1, tagLabelTokens.size)

        when (label) {
            "h1", "h2", "h3", "h4", "h5", "h6" -> applyTagH(args, tagContents, label[1] - '0')
            "p" -> applyTagP(args, tagContents)
            "b" -> applyTagB(args, tagContents)
            "em" -> applyTagEm(args, tagContents)
            else -> {
                Log.w(TAG, "Unhandled html tag: $label")
                FirestoreDataInterface.insertUnhandledHtmlTag(partId, "$label")
                warnIfArgsNotEmpty(label, args)
            }
        }
    }

    /**
     * Returns a [SpannableStringBuilder] representing the provided self-closing html tag.
     *
     * @param[tagLabelTokens] A list containing the tag's label followed by any provided arguments.
     */
    fun applyLoneTag(
        tagLabelTokens: List<CharSequence>
    ): SpannableStringBuilder {
        val label = tagLabelTokens[0]
        val args = tagLabelTokens.subList(1, tagLabelTokens.size)
        return when (label) {
            "br" -> applyTagBr(args)
            "img" -> applyTagImg(tagLabelTokens)
            else -> {
                Log.w(TAG, "Unhandled html tag: <${tagLabelTokens.joinToString(" ")} >")
                FirestoreDataInterface.insertUnhandledHtmlTag(partId, "$label")
                warnIfArgsNotEmpty(label, args)
                SpannableStringBuilder(label)
            }
        }
    }

    /* Pair tag handlers */

    private fun applyTagH(args: List<CharSequence>, contents: SpannableStringBuilder, hSize: Int) {
        warnIfArgsNotEmpty("h$hSize", args)
        applyHeaderSpans(contents, hSize)
        contents.insert(0, "\n")
        contents.append("\n")
    }

    private fun applyTagP(args: List<CharSequence>, contents: SpannableStringBuilder) {
        var centered = false
        for (arg in args) {
            when (arg) {
                "class=\"centerp\"" -> {
                    applySpans(contents, AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER))
                    centered = true
                }
                else -> {
                    Log.w(TAG, "Unhandled arg for tag \"p\": $arg")
                    FirestoreDataInterface.insertUnhandledHtmlArg(partId, "p", "$arg")
                }
            }
        }
        if (!centered) {
            applySpans(contents, LeadingMarginSpan.Standard(100, 0))
        }
        contents.append("\n")
    }

    private fun applyTagB(args: List<CharSequence>, contents: SpannableStringBuilder) {
        warnIfArgsNotEmpty("b", args)
        applySpans(contents, StyleSpan(Typeface.BOLD))
    }

    private fun applyTagEm(args: List<CharSequence>, contents: SpannableStringBuilder) {
        warnIfArgsNotEmpty("em", args)
        applySpans(contents, StyleSpan(Typeface.ITALIC))
    }

    /* Lone tag handlers */

    private fun applyTagBr(args: List<CharSequence>): SpannableStringBuilder {
        warnIfArgsNotEmpty("br", args)
        return SpannableStringBuilder("\n")
    }

    private fun applyTagImg(tokens: List<CharSequence>): SpannableStringBuilder {
        // Use the built-in parser to inject a placeholder image.
        val imgSpan = Html.fromHtml("<${tokens.joinToString(" ")} >", 0)
        return SpannableStringBuilder(imgSpan).apply {
            // Add extra spacing between image and surrounding content
            insert(0, "\n")
            append("\n\n")
        }
    }

    /* Other stuff */

    private fun applyHeaderSpans(contents: SpannableStringBuilder, headerNum: Int) {
        applySpans(
            contents,
            RelativeSizeSpan(HEADING_SIZES[headerNum - 1]),
            StyleSpan(Typeface.BOLD)
        )
        contents.append("\n")
    }

    private fun applySpans(target: Spannable, vararg spans: Any) {
        for (span in spans) {
            target.setSpan(span, 0, target.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun warnIfArgsNotEmpty(label: CharSequence, args: List<CharSequence>) {
        if (args.isEmpty()) return
        Log.w(TAG, "Unhandled args for tag \"$label\": $args")
        for (arg in args) {
            FirestoreDataInterface.insertUnhandledHtmlArg(partId, "$label", "$arg")
        }
    }
}