package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.lonetags

import android.text.SpannableStringBuilder
import android.util.Log
import com.ytrewqwert.yetanotherjnovelreader.data.firebase.FirestoreDataInterface
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags.LoneTagApplier

/**
 * A dummy LoneTagApplier for unhandled tags. Reports the tag and returns the original tag as a
 * [SpannableStringBuilder].
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to.
 * @param[tag] The name of the unhandled tag.
 */
class DummyLoneTagApplier(private val partId: CharSequence, private val tag: CharSequence)
    : LoneTagApplier(partId) {

    companion object {
        private const val TAG = "DummyPairTagApplier"
    }

    override fun apply(args: List<Pair<CharSequence, CharSequence>>): SpannableStringBuilder {
        Log.w(TAG, "Unhandled lone html tag: $tag")
        FirestoreDataInterface.insertUnhandledHtmlTag("$partId", "$tag")
        warnIfArgsNotEmpty(tag, args)

        val argsCombinedTypeValue = args.map { "${it.first}=${it.second}" }
        val fullTag = "<$tag ${argsCombinedTypeValue.joinToString(" ")}/>"
        return SpannableStringBuilder(fullTag)
    }
}