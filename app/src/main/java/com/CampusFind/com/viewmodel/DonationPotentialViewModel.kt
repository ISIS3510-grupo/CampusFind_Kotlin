package com.CampusFind.com.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

data class DonationCategoryResult(
    val category: String,
    val count: Int
)

class DonationPotentialViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    var results by mutableStateOf<List<DonationCategoryResult>>(emptyList())
        private set

    var semesterId by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadDonationPotential() {
        isLoading = true
        errorMessage = null

        firestore
            .collection("appConfig")
            .document("general")
            .get()
            .addOnSuccessListener { config ->

                val currentSemester =
                    config.getString("currentSemesterId") ?: ""

                semesterId = currentSemester

                if (currentSemester.isBlank()) {
                    isLoading = false
                    errorMessage = "Current semester is not configured."
                    return@addOnSuccessListener
                }

                loadFoundItems(currentSemester)
            }
            .addOnFailureListener {
                isLoading = false
                errorMessage = it.message
            }
    }

    private fun loadFoundItems(currentSemester: String) {

        firestore
            .collection("foundItems")
            .whereEqualTo("semesterId", currentSemester)
            .get()
            .addOnSuccessListener { snapshot ->

                results = snapshot.documents
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

                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
                errorMessage = it.message
            }
    }
}