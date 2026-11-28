package com.CampusFind.com.data

import com.CampusFind.com.model.LostReport
import com.google.firebase.firestore.FirebaseFirestore

class AdminLostReportsRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun loadReports(
        onSuccess: (List<LostReport>) -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("claims")
            .whereEqualTo("status", "pending")
            .get()
            .addOnSuccessListener { claims ->

                val reportIds = claims.documents
                    .filter { !it.getString("foundItemId").isNullOrBlank() }
                    .mapNotNull { it.getString("reportId") }
                    .toSet()

                if (reportIds.isEmpty()) {
                    onSuccess(emptyList())
                    return@addOnSuccessListener
                }

                firestore.collection("lostReports")
                    .get()
                    .addOnSuccessListener { snapshot ->

                        val reports = snapshot.documents
                            .filter { document ->
                                val status = document.getString("status")
                                    ?.lowercase() ?: ""

                                document.id in reportIds &&
                                        status != "claimed" &&
                                        status != "closed"
                            }
                            .map { document ->
                                LostReport(
                                    id = document.id,
                                    ownerUid = document.getString("ownerUid") ?: "",
                                    title = document.getString("title") ?: "",
                                    category = document.getString("category") ?: "",
                                    locationName = document.getString("locationName") ?: "",
                                    status = document.getString("status") ?: "",
                                    reportedAt = document.getTimestamp("reportedAt"),
                                    description = document.getString("description") ?: "",
                                    latitude = (document.get("latitude") as? Number)?.toDouble(),
                                    longitude = (document.get("longitude") as? Number)?.toDouble()
                                )
                            }
                            .sortedByDescending { it.reportedAt }

                        onSuccess(reports)
                    }
                    .addOnFailureListener {
                        onError(it.localizedMessage ?: "Unable to load reports.")
                    }
            }
            .addOnFailureListener {
                onError(it.localizedMessage ?: "Unable to load pending claims.")
            }
    }
}
