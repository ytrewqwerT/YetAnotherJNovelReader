package com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.tags

import android.text.Spannable
import android.text.SpannableStringBuilder
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

    /** The html tag's label (e.g. "em") */
    protected abstract val tagString: CharSequence

    /** Attempts to apply the given [args] to [contents]. Unhandled args are reported to the Firestore. */
    protected fun applyArgs(contents: SpannableStringBuilder, args: List<Pair<CharSequence, CharSequence>>) {
        for (arg in args) {
            val (type, value) = arg
            val applier = TagArgApplier.getApplier(type)
            if (applier?.applyArg(value, contents) != true) {
                reportUnhandledArg("$type=$value")
            } else {
                Log.w(TAG, "Handled arg for tag \"$tagString\": $args")
            }
        }
    }

    /** Logs any unhandled [args] to the linked Firestore. */
    protected fun reportUnhandledArg(vararg args: CharSequence) {
        Log.w(TAG, "Unhandled arg for tag \"$tagString\": $args")
        for (arg in args) {
            FirestoreDataInterface.insertUnhandledHtmlArg("$partId", "$tagString", "$arg")
        }
    }

    /** Logs a warning and reports any (unhandled) arguments to the linked Firestore. */
    protected fun warnIfArgsNotEmpty(args: List<Pair<CharSequence, CharSequence>>) {
        if (args.isEmpty()) return
        val combinedTypeValue = args.map { "${it.first}=${it.second}" }
        reportUnhandledArg(tagString, *combinedTypeValue.toTypedArray())
    }
}