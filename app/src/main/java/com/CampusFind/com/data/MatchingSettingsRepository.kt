package com.CampusFind.com.data

import com.google.firebase.firestore.FirebaseFirestore

class MatchingSettingsRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    fun getMatchingThreshold(
        onSuccess: (Double) -> Unit,
        onError: (String) -> Unit
    ) {

        firestore
            .collection("appConfig")
            .document("general")
            .get()
            .addOnSuccessListener { document ->

                val threshold =
                    (document.get("matchingThreshold") as? Number)
                        ?.toDouble()

                if (
                    threshold != null &&
                    threshold in 0.0..1.0
                ) {
                    onSuccess(
                        threshold
                    )
                } else {
                    onSuccess(
                        0.70
                    )
                }
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.localizedMessage
                        ?: "Unable to load matching settings."
                )
            }
    }
}