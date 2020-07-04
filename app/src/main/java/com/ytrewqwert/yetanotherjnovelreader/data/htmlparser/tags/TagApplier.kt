package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags

import android.text.Spannable
import android.util.Log
import com.ytrewqwert.yetanotherjnovelreader.data.firebase.FirestoreDataInterface

/**
 * Parent class that exposes methods for subclasses to report unhandled html tag arguments.
 *
 * @param[partId] The id of the source part to attribute unhandled arguments to.
 */
abstract class TagApplier(private val partId: CharSequence) {
    companion object {
        private const val TAG = "TagApplier"

        /** Applies [spans], exclusive-exclusive, to the entire length of [target]. */
        fun applySpans(target: Spannable, vararg spans: Any) {
            for (span in spans) {
                target.setSpan(span, 0, target.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    /** Logs any unhandled [args] from a given html [tag] to the linked Firestore. */
    protected fun reportUnhandledArg(tag: CharSequence, vararg args: CharSequence) {
        for (arg in args) {
            FirestoreDataInterface.insertUnhandledHtmlArg("$partId", "$tag", "$arg")
        }
    }

    /** Logs a warning and reports any (unhandled) arguments to the linked Firestore. */
    protected fun warnIfArgsNotEmpty(tag: CharSequence, args: List<Pair<CharSequence, CharSequence>>) {
        if (args.isEmpty()) return
        Log.w(TAG, "Unhandled args for tag \"$tag\": $args")
        val combinedTypeValue = args.map { "${it.first}=${it.second}" }
        reportUnhandledArg(tag, *combinedTypeValue.toTypedArray())
    }
}