package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser

import android.text.SpannableStringBuilder
import android.text.Spanned
import java.util.ArrayDeque

/** Singleton exposing a function for converting html to a [Spanned]. */
object PartHtmlParser {
    /**
     * Converts the provided [html] (containing a part's contents) into a [Spanned].
     *
     * @param[partId] The ID of the part, used for logging unexpected html tags/args.
     */
    fun parse(html: String, partId: String): Spanned {
        val tagApplier = HtmlTagApplier(partId)
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
                        tagApplier.applyTagPair(openTag.tagLabelTokens, openTag.contents)
                        tagStack.first.contents.append(openTag.contents)
                    } else {
                        // Out-of-sequence closing tag? Treat as self-closing tag
                        tagStack.first.contents.append(tagApplier.applyLoneTag(tagLabelTokens))
                    }
                }
                noNewLine[match.range.last - 1] == '/' -> {
                    // Self-closing tag
                    tagStack.first.contents.append(tagApplier.applyLoneTag(tagLabelTokens))
                }
                else -> {
                    // Opening tag
                    // Extract the label and add to the tag stack.
                    tagStack.addFirst(
                        IncompleteTag(
                            tagLabelTokens
                        )
                    )
                }
            }
            searchStartIndex = match.range.last + 1
            match = tagRegex.find(noNewLine, searchStartIndex)
        }

        val tail = noNewLine.subSequence(searchStartIndex, noNewLine.length)
        tagStack.first.contents.append(tail)
        return tagStack.first.contents
    }

    private data class IncompleteTag(
        val tagLabelTokens: List<CharSequence>,
        var contents: SpannableStringBuilder = SpannableStringBuilder()
    )
}