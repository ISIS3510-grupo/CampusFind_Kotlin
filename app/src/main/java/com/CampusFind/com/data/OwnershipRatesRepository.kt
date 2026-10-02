package com.CampusFind.com.data

import com.CampusFind.com.service.ClaimMatcher
import com.CampusFind.com.service.ClaimOutcome
import com.CampusFind.com.service.OwnershipRateCalculator
import com.CampusFind.com.service.OwnershipRates
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class OwnershipRatesRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun getOwnershipRates(
        onSuccess: (OwnershipRates) -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("claims")
            .get()
            .addOnSuccessListener { snapshot ->
                val claims = snapshot.documents.filter { claim ->
                    val foundItemId = claim.getString("foundItemId") ?: ""
                    val reportId = claim.getString("reportId") ?: ""
                    foundItemId.isNotBlank() && !foundItemId.contains("/") &&
                            reportId.isNotBlank() && !reportId.contains("/")
                }
                readClaim(claims, 0, mutableListOf(), onSuccess, onError)
            }
            .addOnFailureListener { exception ->
                onError(exception.localizedMessage ?: "Unable to load claims.")
            }
    }

    private fun readClaim(
        claims: List<DocumentSnapshot>,
        index: Int,
        outcomes: MutableList<ClaimOutcome>,
        onSuccess: (OwnershipRates) -> Unit,
        onError: (String) -> Unit
    ) {
        if (index >= claims.size) {
            onSuccess(OwnershipRateCalculator.calculate(outcomes))
            return
        }

        val claim = claims[index]
        val foundItemId = claim.getString("foundItemId") ?: ""
        val reportId = claim.getString("reportId") ?: ""
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
                        firestore.collection("lostReportPrivate")
                            .document(reportId)
                            .get()
                            .addOnSuccessListener { privateReport ->
                                val result = ClaimMatcher.compare(registered, answer)
                                outcomes.add(ClaimOutcome(privateReport.exists(), result.verdict))
                                readClaim(claims, index + 1, outcomes, onSuccess, onError)
                            }
                            .addOnFailureListener { exception ->
                                onError(exception.localizedMessage ?: "Unable to load private report.")
                            }
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
