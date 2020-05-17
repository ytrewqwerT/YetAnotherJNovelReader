package com.ytrewqwert.yetanotherjnovelreader.data

import android.graphics.Typeface
import android.text.*
import android.text.style.AlignmentSpan
import android.text.style.LeadingMarginSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import java.util.*

object PartHtmlParser {
    private const val TAG = "PartHtmlParser"

    private val HEADING_SIZES = arrayOf(1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1f)

    fun parse(html: String): Spanned {
        val noNewLine = html.replace("\n", "")
        val tagRegex = Regex("<[^>]*>")
        val tagStack = ArrayDeque<IncompleteTag>()
        tagStack.addFirst(IncompleteTag(listOf(""))) // Dummy tag to store processed contents

        var searchStartIndex = 0
        var match = tagRegex.find(noNewLine, searchStartIndex)
        while (match != null) {
            // Append any skipped text to the parent tag before processing the current match.
            tagStack.first.contents.append(noNewLine.subSequence(searchStartIndex, match.range.first))

            val tagLabelTokens = match.value.trim('<', '/', '>', ' ').split(' ')
            when {
                noNewLine[match.range.first + 1] == '/' -> {
                    // Closing tag
                    val openTag = tagStack.first
                    if (openTag.tagLabelTokens[0] == tagLabelTokens[0]) {
                        tagStack.removeFirst()
                        processTagPair(openTag.tagLabelTokens, openTag.contents)
                        tagStack.first.contents.append(openTag.contents)
                    } else {
                        // Out-of-sequence closing tag? Treat as self-closing tag
                        tagStack.first.contents.append(processLoneTag(tagLabelTokens))
                    }
                }
                noNewLine[match.range.last - 1] == '/' -> {
                    // Self-closing tag
                    tagStack.first.contents.append(processLoneTag(tagLabelTokens))
                }
                else -> {
                    // Opening tag
                    // Extract the label and add to the tag stack.
                    tagStack.addFirst(IncompleteTag(tagLabelTokens))
                }
            }
            searchStartIndex = match.range.last + 1
            match = tagRegex.find(noNewLine, searchStartIndex)
        }

        val tail = noNewLine.subSequence(searchStartIndex, noNewLine.length)
        tagStack.first.contents.append(tail)
        return tagStack.first.contents
    }

    private fun processTagPair(
        tagLabelTokens: List<CharSequence>, tagContents: SpannableStringBuilder
    ) {
        var appendNewLine = false

        val label = tagLabelTokens[0]
        val args = tagLabelTokens.subList(1, tagLabelTokens.size)

        when (label) {
            "h1", "h2", "h3", "h4", "h5", "h6" -> {
                warnIfArgsNotEmpty(label, args)
                applyHeaderSpans(tagContents, label[1] - '0')
                appendNewLine = true
            }
            "p" -> {
                var centered = false
                for (arg in args) {
                    when (arg) {
                        "class=\"centerp\"" -> {
                            applySpans(tagContents, AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER))
                            centered = true
                        }
                        else -> {
                            Log.w(TAG, "Unhandled arg for tag \"p\": $arg")
                        }
                    }
                }
                if (!centered) {
                    applySpans(tagContents, LeadingMarginSpan.Standard(100, 0))
                }
                appendNewLine = true
            }
            "b" -> {
                warnIfArgsNotEmpty(label, args)
                applySpans(tagContents, StyleSpan(Typeface.BOLD))
            }
            "em" -> {
                warnIfArgsNotEmpty(label, args)
                applySpans(tagContents, StyleSpan(Typeface.ITALIC))
            }
            else -> {
                Log.w(TAG, "Unhandled html tag: $label")
                warnIfArgsNotEmpty(label, args)
            }
        }
        if (appendNewLine) tagContents.append("\n")
    }

    private fun processLoneTag(
        tagLabelTokens: List<CharSequence>
    ): SpannableStringBuilder {
        val label = tagLabelTokens[0]
        val args = tagLabelTokens.subList(1, tagLabelTokens.size)
        return when (label) {
            "br" -> {
                if (args.isNotEmpty()) Log.w(TAG, "Unhandled args for html tag \"br\": $args")
                SpannableStringBuilder("\n")
            }
            "img" -> {
                // Use the built-in parser to inject a placeholder image.
                val imgSpan = Html.fromHtml("<${tagLabelTokens.joinToString(" ")} >", 0)
                return SpannableStringBuilder(imgSpan).apply {
                    // Add extra spacing between image and surrounding content
                    insert(0, "\n")
                    append("\n\n")
                }
            }
            else -> {
                Log.w(TAG, "Unhandled html tag: <${tagLabelTokens.joinToString(" ")} >")
                SpannableStringBuilder(label)
            }
        }
    }

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
    }

    private data class IncompleteTag(
        val tagLabelTokens: List<CharSequence>,
        var contents: SpannableStringBuilder = SpannableStringBuilder()
    )
}