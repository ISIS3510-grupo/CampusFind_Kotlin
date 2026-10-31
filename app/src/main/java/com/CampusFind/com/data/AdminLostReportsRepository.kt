package com.CampusFind.com.data

import com.CampusFind.com.model.LostReport
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AdminLostReportsRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun loadReports(onSuccess: (List<LostReport>) -> Unit, onError: (String) -> Unit) {
        firestore.collection("lostReports")
            .whereEqualTo("status", "reported")
            .get()
            .addOnSuccessListener { snapshot ->
                onSuccess(snapshot.documents.map { document ->
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
                }.sortedByDescending { it.reportedAt })
            }
            .addOnFailureListener { onError(it.localizedMessage ?: "Unable to load lost reports.") }
    }

    fun markAsFound(reportId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val reportRef = firestore.collection("lostReports").document(reportId)
        reportRef.get()
            .addOnSuccessListener { document ->
                val ownerUid = document.getString("ownerUid")
                val category = document.getString("category")
                if (document.getString("status") != "reported" || ownerUid.isNullOrBlank() || category.isNullOrBlank()) {
                    onError("The report is no longer reported or its information is incomplete.")
                    return@addOnSuccessListener
                }
                val batch = firestore.batch()
                batch.update(reportRef, mapOf(
                    "status" to "found",
                    "statusChangedAt" to FieldValue.serverTimestamp(),
                    "foundAt" to FieldValue.serverTimestamp()
                ))
                batch.set(firestore.collection("reportStatusEvents").document("${reportId}_found"), mapOf(
                    "reportId" to reportId,
                    "ownerUid" to ownerUid,
                    "category" to category,
                    "fromStatus" to "reported",
                    "toStatus" to "found",
                    "occurredAt" to FieldValue.serverTimestamp()
                ))
                batch.commit()
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onError(it.localizedMessage ?: "Unable to mark report as found.") }
            }
            .addOnFailureListener { onError(it.localizedMessage ?: "Unable to load lost report.") }
    }

}
