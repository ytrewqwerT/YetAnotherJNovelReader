package com.ytrewqwert.yetanotherjnovelreader.data.firebase

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/** Exposes functions for interacting with the linked Firebase Firestore. */
object FirestoreDataInterface {
    private val db = Firebase.firestore

    /** Logs an unhandled html [tag] found in [srcPartId] to the Firestore. */
    fun insertUnhandledHtmlTag(srcPartId: String, tag: String) {
        val docPath = "data/unhandled_html/tags/$tag/part_ids/$srcPartId"
        createDocIfNotExists(db.document(docPath))
    }

    /** Logs an unhandled [arg] to a html [tag] found in [srcPartId] to the Firestore. */
    fun insertUnhandledHtmlArg(srcPartId: String, tag: String, arg: String) {
        val docPath = "data/unhandled_html/tags/$tag/args/$arg/part_ids/$srcPartId"
        createDocIfNotExists(db.document(docPath))
    }

    /** Logs an unhandled html character [code] found in [srcPartId] to the Firestore. */
    fun insertUnhandledCharCode(srcPartId: String, code: String) {
        val docPath = "data/unhandled_html/char_codes/$code/part_ids/$srcPartId"
        createDocIfNotExists(db.document(docPath))
    }

    private fun createDocIfNotExists(docRef: DocumentReference) {
        docRef.get().addOnSuccessListener { docSnap ->
            if (docSnap.exists()) return@addOnSuccessListener
            docRef.set(HashMap<String, Any>())
        }
    }
}