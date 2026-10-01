package com.CampusFind.com.data

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class FoundItemRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun saveFoundItem(
        title: String,
        category: String,
        publicDescription: String,
        locationName: String,
        photoUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = firebaseAuth.currentUser

        if (user == null) {
            onError("Please sign in before registering an item.")
            return
        }

        val id = firestore.collection("foundItems").document().id
        val item = mutableMapOf<String, Any>(
            "title" to title,
            "category" to category,
            "publicDescription" to publicDescription,
            "locationName" to locationName,
            "status" to "available",
            "reporterUid" to user.uid,
            "createdAt" to Timestamp.now(),
            "semesterId" to "2026-2",
            "donationEligible" to false,
            "donationStatus" to "none"
        )

        if (photoUri == null) {
            saveDocument(id, item, onSuccess, onError)
            return
        }

        val photoPath = "foundItems/$id.jpg"
        storage.reference.child(photoPath)
            .putFile(photoUri)
            .addOnSuccessListener {
                item["photoPath"] = photoPath
                saveDocument(id, item, onSuccess, onError)
            }
            .addOnFailureListener { exception ->
                onError(exception.localizedMessage ?: "Unable to upload photo.")
            }
    }

    private fun saveDocument(
        id: String,
        item: Map<String, Any>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("foundItems")
            .document(id)
            .set(item)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onError(exception.localizedMessage ?: "Unable to save found item.")
            }
    }
}
