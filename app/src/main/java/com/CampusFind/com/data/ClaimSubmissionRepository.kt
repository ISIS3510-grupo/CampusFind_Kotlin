package com.CampusFind.com.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ClaimSubmissionRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun submitClaim(
        matchId: String,
        ownershipAnswer: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            onError("Please sign in before submitting a claim.")
            return
        }
        if (matchId.isBlank() || ownershipAnswer.isBlank()) {
            onError("Select a match and describe the private details of the item.")
            return
        }
        firestore.collection("matches").document(matchId).get()
            .addOnSuccessListener { document ->
                if (document.getString("ownerUid") != user.uid || auth.currentUser?.uid != user.uid) {
                    onError("You can only submit a claim for your own match.")
                    return@addOnSuccessListener
                }
                val reportId = document.getString("reportId")
                val foundItemId = document.getString("foundItemId")
                if (reportId.isNullOrBlank() || foundItemId.isNullOrBlank()) {
                    onError("The match information is incomplete.")
                    return@addOnSuccessListener
                }
                val claimRef = firestore.collection("claims").document()
                val batch = firestore.batch()
                batch.set(claimRef, mapOf(
                    "reportId" to reportId,
                    "foundItemId" to foundItemId,
                    "claimantUid" to user.uid,
                    "status" to "pending",
                    "createdAt" to FieldValue.serverTimestamp()
                ))
                batch.set(firestore.collection("claimPrivate").document(claimRef.id), mapOf(
                    "ownershipAnswer" to ownershipAnswer
                ))
                batch.commit()
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onError(it.localizedMessage ?: "Unable to submit claim.") }
            }
            .addOnFailureListener { onError(it.localizedMessage ?: "Unable to load match.") }
    }
}
