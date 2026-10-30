package com.CampusFind.com.data

import com.CampusFind.com.model.DonationCategoryResult
import com.google.firebase.firestore.FirebaseFirestore

class DonationPotentialRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun loadDonationPotential(
        onSemester: (String) -> Unit,
        onSuccess: (List<DonationCategoryResult>) -> Unit,
        onError: (String?) -> Unit
    ) {
        firestore
            .collection("appConfig")
            .document("general")
            .get()
            .addOnSuccessListener { config ->

                val currentSemester =
                    config.getString("currentSemesterId") ?: ""

                onSemester(currentSemester)

                if (currentSemester.isBlank()) {
                    onError("Current semester is not configured.")
                    return@addOnSuccessListener
                }

                loadFoundItems(currentSemester, onSuccess, onError)
            }
            .addOnFailureListener {
                onError(it.message)
            }
    }

    private fun loadFoundItems(
        currentSemester: String,
        onSuccess: (List<DonationCategoryResult>) -> Unit,
        onError: (String?) -> Unit
    ) {

        firestore
            .collection("foundItems")
            .whereEqualTo("semesterId", currentSemester)
            .get()
            .addOnSuccessListener { snapshot ->

                val results = snapshot.documents
                    .filter { document ->
                        document.getString("status") == "available" &&
                                document.getBoolean("donationEligible") == true
                    }
                    .groupingBy { document ->
                        document.getString("category")
                            ?: "Unknown"
                    }
                    .eachCount()
                    .map { (category, count) ->
                        DonationCategoryResult(
                            category = category,
                            count = count
                        )
                    }
                    .sortedByDescending {
                        it.count
                    }

                onSuccess(results)
            }
            .addOnFailureListener {
                onError(it.message)
            }
    }
}
