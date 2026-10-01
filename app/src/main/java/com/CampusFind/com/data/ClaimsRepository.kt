package com.CampusFind.com.data

import com.CampusFind.com.model.ClaimReview
import com.CampusFind.com.service.ClaimMatcher
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ClaimsRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun getPendingClaims(
        onSuccess: (List<ClaimReview>) -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("claims")
            .whereEqualTo("status", "pending")
            .get()
            .addOnSuccessListener { snapshot ->
                readClaim(snapshot.documents, 0, mutableListOf(), onSuccess, onError)
            }
            .addOnFailureListener { exception ->
                onError(exception.localizedMessage ?: "Unable to load pending claims.")
            }
    }

    private fun readClaim(
        claims: List<DocumentSnapshot>,
        index: Int,
        reviews: MutableList<ClaimReview>,
        onSuccess: (List<ClaimReview>) -> Unit,
        onError: (String) -> Unit
    ) {
        if (index >= claims.size) {
            onSuccess(reviews)
            return
        }

        val claim = claims[index]
        val foundItemId = claim.getString("foundItemId") ?: ""
        if (foundItemId.isBlank() || foundItemId.contains("/")) {
            onError("Invalid found item ID for claim ${claim.id}.")
            return
        }

        firestore.collection("claimPrivate")
            .document(claim.id)
            .get()
            .addOnSuccessListener { privateClaim ->
                val answer = privateClaim.getString("ownershipAnswer") ?: ""
                firestore.collection("foundItemPrivate")
                    .document(foundItemId)
                    .get()
                    .addOnSuccessListener { privateItem ->
                        val registered = privateItem.getString("privateCharacteristics") ?: ""
                        val result = ClaimMatcher.compare(registered, answer)
                        reviews.add(
                            ClaimReview(
                                claimId = claim.id,
                                reportId = claim.getString("reportId") ?: "",
                                foundItemId = foundItemId,
                                answer = answer,
                                registered = registered,
                                score = result.score,
                                verdict = result.verdict
                            )
                        )
                        readClaim(claims, index + 1, reviews, onSuccess, onError)
                    }
                    .addOnFailureListener { exception ->
                        onError(exception.localizedMessage ?: "Unable to load registered characteristics.")
                    }
            }
            .addOnFailureListener { exception ->
                onError(exception.localizedMessage ?: "Unable to load ownership answer.")
            }
    }
}
