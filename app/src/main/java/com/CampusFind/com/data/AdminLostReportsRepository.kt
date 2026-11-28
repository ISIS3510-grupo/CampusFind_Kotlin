package com.CampusFind.com.data

import com.CampusFind.com.model.LostReport
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Source

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

    fun markAsFound(
        reportId: String,
        foundLocation: String,
        privateCharacteristics: String,
        existingFoundItemId: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val staffUid = FirebaseAuth.getInstance().currentUser?.uid
        val existingId = existingFoundItemId?.trim()?.takeIf { it.isNotEmpty() }

        if (staffUid.isNullOrBlank()) {
            onError("Please sign in.")
            return
        }

        if (reportId.isBlank() || reportId.contains("/") ||
            (existingId != null && existingId.contains("/"))
        ) {
            onError("Invalid item ID.")
            return
        }

        if (existingId == null &&
            (foundLocation.isBlank() || privateCharacteristics.isBlank())
        ) {
            onError("Enter the found location and private detail.")
            return
        }

        val reportRef = firestore.collection("lostReports").document(reportId)
        val itemId = existingId ?: "from_lost_$reportId"
        val itemRef = firestore.collection("foundItems").document(itemId)
        val eventRef = firestore.collection("reportStatusEvents")
            .document("${reportId}_found")

        firestore.runTransaction { tx ->

            val report = tx.get(reportRef)
            val item = tx.get(itemRef)

            check(report.exists() && report.getString("status") == "reported") {
                "Report is no longer available."
            }

            val title = report.getString("title")
                ?.takeIf { it.isNotBlank() } ?: error("Missing title.")
            val category = report.getString("category")
                ?.takeIf { it.isNotBlank() } ?: error("Missing category.")
            val ownerUid = report.getString("ownerUid")
                ?.takeIf { it.isNotBlank() } ?: error("Missing owner.")

            if (existingId == null) {
                check(!item.exists()) { "Inventory item already exists." }

                val data = FoundItemFactory.create(
                    title = title,
                    category = category,
                    publicDescription = "",
                    locationName = foundLocation.trim(),
                    reporterUid = staffUid,
                    photoPath = null
                ).toMutableMap()

                data["createdAt"] = FieldValue.serverTimestamp()
                data["sourceLostReportId"] = reportId

                tx.set(itemRef, data)
                tx.set(
                    firestore.collection("foundItemPrivate").document(itemId),
                    FoundItemFactory.createPrivate(
                        privateCharacteristics.trim()
                    )
                )
            } else {
                check(item.exists() && item.getString("status") == "available") {
                    "Found item is not available."
                }

                val linkedReport = item.getString("sourceLostReportId")
                check(linkedReport.isNullOrBlank() || linkedReport == reportId) {
                    "Item is already linked to another report."
                }

                tx.update(itemRef, "sourceLostReportId", reportId)
            }

            tx.update(reportRef, mapOf(
                "status" to "found",
                "foundAt" to FieldValue.serverTimestamp(),
                "statusChangedAt" to FieldValue.serverTimestamp(),
                "linkedFoundItemId" to itemId
            ))

            tx.set(eventRef, mapOf(
                "reportId" to reportId,
                "ownerUid" to ownerUid,
                "category" to category,
                "fromStatus" to "reported",
                "toStatus" to "found",
                "linkedFoundItemId" to itemId,
                "occurredAt" to FieldValue.serverTimestamp()
            ))

            true
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onError(it.localizedMessage ?: "Unable to mark item as found.")
        }
    }

}
