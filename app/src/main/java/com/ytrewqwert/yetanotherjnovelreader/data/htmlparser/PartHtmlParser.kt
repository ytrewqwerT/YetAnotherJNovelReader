package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser

import android.text.SpannableStringBuilder
import android.text.Spanned
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.LoneTagApplier
import java.util.ArrayDeque

/** Singleton exposing a function for converting html to a [Spanned]. */
object PartHtmlParser {

    /**
     * Converts the provided [html] (containing a part's contents) into a [Spanned].
     *
     * @param[partId] The ID of the part, used for logging unexpected html tags/args.
     */
    fun parse(html: String, partId: String): Spanned {
        val noXmlWhitespace = cleanXmlWhitespace(html)
        val noTags = parseTags(noXmlWhitespace, partId)
        return HtmlCharCodeConverter(partId).processHtml(noTags)
    }

    private fun cleanXmlWhitespace(html: String): String {
        val cleaned = StringBuilder(html)
        var i = 0
        while (i < cleaned.length) {
            if (cleaned[i] == '\n') {
                var j = i + 1
                while (j < cleaned.length && cleaned[j] == ' ') j++
                cleaned.deleteRange(i, j)
            } else {
                i++
            }
        }
        return cleaned.toString()
    }

    private fun parseTags(html: String, partId: String): SpannableStringBuilder {
        val tagApplier = HtmlTagApplier(partId)
        // Initialise with a dummy top-level tag to store processed contents
        val tagStack = ArrayDeque(listOf(IncompleteTag("")))

        var searchStartIndex = 0
        while (searchStartIndex < html.length) {
            val tagRegex = Regex("<[^>]*>")
            val match = tagRegex.find(html, searchStartIndex) ?: break

            // Append any skipped text to the parent tag before processing the current match.
            tagStack.first.contents.append(html.subSequence(searchStartIndex, match.range.first))

            // Check for and process/skip html comments
            if (match.value.startsWith("<!--")) {
                val commentEndRegex = Regex("-->")
                val endMatch = commentEndRegex.find(html, match.range.first + 4)
                searchStartIndex = if (endMatch != null) endMatch.range.last + 1 else html.length
                continue
            }

            // Skip the initial "<?xml ...>
            if (match.value.startsWith("<?xml")) {
                searchStartIndex = match.range.last + 1
                continue
            }

            // Skip the initial "<!DOCTYPE html>
            if (match.value.startsWith("<!DOCTYPE")) {
                searchStartIndex = match.range.last + 1
                continue
            }

            val fullTag = match.value.trim('<', '/', '>', ' ')
            val (tagLabel, tagArgs) = extractTagComponents(fullTag)

            // Geez, this is disgusting...
            if (html[match.range.first + 1] == '/') { // Closing tag
                if (tagStack.first.tagLabel == tagLabel)
                    reduceTagStackPair(tagStack, tagApplier)
                else // Treat out-of-sequence closing tag as self-closing
                    tagStack.first.contents.append(tagApplier.applyLoneTag(tagLabel, tagArgs))
            } else { // Opening tag. Apply if LoneTag, or add to the tag stack if PairTag.
                if (LoneTagApplier.tagIsLone(tagLabel, partId))
                    tagStack.first.contents.append(tagApplier.applyLoneTag(tagLabel, tagArgs))
                else
                    tagStack.addFirst(IncompleteTag(tagLabel, tagArgs))
            }

            searchStartIndex = match.range.last + 1
        }

        // Append any trailing text then close any unclosed tags (which ideally shouldn't happen)
        val tail = html.subSequence(searchStartIndex, html.length)
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