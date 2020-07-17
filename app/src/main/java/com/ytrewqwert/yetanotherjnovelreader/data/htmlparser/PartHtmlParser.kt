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
        val noCharCodes = HtmlCharCodeConverter(partId).processHtml(html)
        val noNewLines = noCharCodes.replace("\n", "")
        val tagApplier = HtmlTagApplier(partId)
        // Initialise with a dummy top-level tag to store processed contents
        val tagStack = ArrayDeque<IncompleteTag>(listOf(IncompleteTag("")))

        val tagRegex = Regex("<[^>]*>")
        var searchStartIndex = 0
        var match = tagRegex.find(noNewLines, searchStartIndex)
        while (match != null) {
            // Append any skipped text to the parent tag before processing the current match.
            tagStack.first.contents.append(noNewLines.subSequence(searchStartIndex, match.range.first))

            val fullTag = match.value.trim('<', '/', '>', ' ')
            val (tagLabel, tagArgs) = extractTagComponents(fullTag)
            when {
                noNewLines[match.range.first + 1] == '/' -> { // Closing tag
                    if (tagStack.first.tagLabel == tagLabel) {
                        reduceTagStackPair(tagStack, tagApplier)
                    } else { // Treat out-of-sequence closing tag as self-closing
                        tagStack.first.contents.append(tagApplier.applyLoneTag(tagLabel, tagArgs))
                    }
                }
                noNewLines[match.range.last - 1] == '/' -> { // Self-closing tag
                    tagStack.first.contents.append(tagApplier.applyLoneTag(tagLabel, tagArgs))
                }
                else -> { // Opening tag. Extract the label and add to the tag stack.
                    tagStack.addFirst(IncompleteTag(tagLabel, tagArgs))
                }
            }
            searchStartIndex = match.range.last + 1
            match = tagRegex.find(noNewLines, searchStartIndex)
        }

        val tail = noNewLines.subSequence(searchStartIndex, noNewLines.length)
        tagStack.first.contents.append(tail)
        while (tagStack.size > 1) reduceTagStackPair(tagStack, tagApplier)
        return tagStack.first.contents
    }

    private fun extractTagComponents(fullTag: String): Pair<CharSequence, List<Pair<CharSequence, CharSequence>>> {
        var tagLabelEnd = fullTag.indexOf(' ')
        if (tagLabelEnd == -1) tagLabelEnd = fullTag.length
        val tagLabel = fullTag.subSequence(0, tagLabelEnd)
        val tagArgStr = fullTag.subSequence(tagLabelEnd, fullTag.length).trim(' ')
        val tagArgs = parseArgs(tagArgStr)
        return Pair(tagLabel, tagArgs)
    }

    private fun reduceTagStackPair(
        tagStack: ArrayDeque<IncompleteTag>, tagApplier: HtmlTagApplier
    ) {
        val openTag = tagStack.first
        tagStack.removeFirst()
        tagApplier.applyTagPair(openTag.tagLabel, openTag.tagArgs, openTag.contents)
        tagStack.first.contents.append(openTag.contents)
    }

    private fun parseArgs(argStr: CharSequence): List<Pair<CharSequence, CharSequence>> {
        val resultList = ArrayList<Pair<CharSequence, CharSequence>>()
        val trimmedArgStr  = argStr.trim()

        val argRegex = Regex("\\S+\\s?=\\s?\"[^\"]+\"")
        val matches = argRegex.findAll(trimmedArgStr)
        for (match in matches) {
            val argText = match.value
            val splitPoint = argText.indexOf('=')
            val arg = argText.subSequence(0, splitPoint).trim()
            val value = argText.subSequence(splitPoint+1, argText.length).trim()
            resultList.add(Pair(arg, value))
        }
        return resultList
    }

    private data class IncompleteTag(
        val tagLabel: CharSequence,
        val tagArgs: List<Pair<CharSequence, CharSequence>> = emptyList(),
        var contents: SpannableStringBuilder = SpannableStringBuilder()
    )
}