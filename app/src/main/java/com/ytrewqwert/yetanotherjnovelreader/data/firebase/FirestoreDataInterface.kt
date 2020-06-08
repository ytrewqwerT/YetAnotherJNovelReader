package com.ytrewqwert.yetanotherjnovelreader.data.firebase

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirestoreDataInterface {
    private val db = Firebase.firestore

    fun insertUnhandledHtmlTag(srcPartId: String, tag: String) {
        val docRef = db.document("data/unhandled_html/tags/$tag")
        addToPartIdList(docRef, srcPartId)
    }

    fun insertUnhandledHtmlArg(srcPartId: String, tag: String, arg: String) {
        val docRef = db.document("data/unhandled_html/tags/$tag/args/$arg")
        addToPartIdList(docRef, srcPartId)
    }

    private fun addToPartIdList(docRef: DocumentReference, partId: String) {
        docRef.get().addOnSuccessListener { docSnap ->
            val docData = docSnap.data ?: HashMap<String, Any>()
            val partIds = docData["part_ids"] as? ArrayList<Any> ?: ArrayList()
            if (partIds.contains(partId)) return@addOnSuccessListener

            partIds.add(partId)
            docData["part_ids"] = partIds // In case a new list was created
            docRef.set(docData)
        }
    }
}