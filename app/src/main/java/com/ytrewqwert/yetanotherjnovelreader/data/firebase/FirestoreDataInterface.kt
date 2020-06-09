package com.ytrewqwert.yetanotherjnovelreader.data.firebase

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirestoreDataInterface {
    private val db = Firebase.firestore

    fun insertUnhandledHtmlTag(srcPartId: String, tag: String) {
        val docPath = "data/unhandled_html/tags/$tag/part_ids/$srcPartId"
        val docRef = db.document(docPath)
        createDocIfNotExists(docRef)
    }

    fun insertUnhandledHtmlArg(srcPartId: String, tag: String, arg: String) {
        val docPath = "data/unhandled_html/tags/$tag/args/$arg/part_ids/$srcPartId"
        val docRef = db.document(docPath)
        createDocIfNotExists(docRef)
    }

    private fun createDocIfNotExists(docRef: DocumentReference) {
        docRef.get().addOnSuccessListener { docSnap ->
            if (docSnap.exists()) return@addOnSuccessListener
            docRef.set(HashMap<String, Any>())
        }
    }
}