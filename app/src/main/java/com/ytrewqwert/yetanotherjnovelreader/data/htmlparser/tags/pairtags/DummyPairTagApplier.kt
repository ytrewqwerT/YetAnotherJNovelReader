package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.pairtags

import android.text.SpannableStringBuilder
import android.util.Log
import com.ytrewqwert.yetanotherjnovelreader.data.firebase.FirestoreDataInterface
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.PairTagApplier

/**
 * A dummy PairTagApplier for unhandled tags. Reports the tag and pushes the original tag into the
 * result.
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to.
 * @param[tag] The name of the unhandled tag.
 */
class DummyPairTagApplier(private val partId: CharSequence, private val tag: CharSequence)
    : PairTagApplier(partId) {

    companion object {
        private const val TAG = "DummyPairTagApplier"
    }

    override fun apply(
        args: List<Pair<CharSequence, CharSequence>>, contents: SpannableStringBuilder
    ) {
        Log.w(TAG, "Unhandled html tag pair: $tag")
        FirestoreDataInterface.insertUnhandledHtmlTag("$partId", "$tag")
        warnIfArgsNotEmpty(tag, args)

        val argsCombinedTypeValue = args.map { "${it.first}=${it.second}" }
        val tokens = listOf(tag, *argsCombinedTypeValue.toTypedArray())
        val openTag = "<${tokens.joinToString(" ")}>"
        val closeTag = "<$tag />"
        contents.insert(0, openTag)
        contents.append(closeTag)
    }
}