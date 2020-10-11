package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser

import android.text.SpannableStringBuilder
import android.util.Log
import com.ytrewqwert.yetanotherjnovelreader.data.firebase.FirestoreDataInterface

/**
 * Class for processing html character codes.
 *
 * @property[partId] The source part to attribute any unhandled codes to when logging.
 */
class HtmlCharCodeConverter(private val partId: String) {
    companion object {
        private const val TAG = "HtmlCharCodeConverter"
    }

    /** Converts html character codes found in the [html] to their respective characters. */
    fun processHtml(html: SpannableStringBuilder): SpannableStringBuilder {
        val codeRegex = Regex("&[^;]*;")
        var curPos = 0
        var match = codeRegex.find(html, curPos)
        while (match != null) {
            val code = match.value.trim('&', ';')
            val converted = convertCharCode(code)
            html.replace(match.range.first, match.range.last + 1, converted)
            curPos = match.range.first + 1
            match = codeRegex.find(html, curPos)
        }
        return html
    }

    // Should probably generalise the replacements of "#xxx" style codes.
    private fun convertCharCode(code: String): String = when (code) {
        "#34", "quot" -> "\""
        "#38", "amp" -> "&"
        "#39", "apos" -> "'"
        "#60", "lt" -> "<"
        "#62", "gt" -> ">"
        "#8195", "emsp" -> "\u2003"
        else -> {
            Log.w(TAG, "Unhandled html character code: &$code;")
            FirestoreDataInterface.insertUnhandledCharCode(partId, code)
            "&$code;"
        }
    }
}