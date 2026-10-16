package com.CampusFind.com.data

import com.CampusFind.com.model.ClaimReview
import com.CampusFind.com.service.ClaimMatcher
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

/** Carga reclamos pendientes y une las respuestas privadas con los detalles privados de los objetos. */
class ClaimsRepository {

    private val firestore = FirebaseFirestore.getInstance()

    /** Consulta claims con status pending y entrega las comparaciones al completar las lecturas; comunica fallos mediante onError. */
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

    /** Lee claimPrivate y foundItemPrivate para cada reclamo, ejecuta ClaimMatcher y acumula ClaimReview. Un ID inválido o una lectura fallida detiene el proceso. */
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
