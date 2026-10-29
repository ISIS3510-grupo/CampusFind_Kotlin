package com.CampusFind.com.data

import com.CampusFind.com.model.LostReport
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class LostReportRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun observeReports(ownerUid: String, onUpdate: (List<LostReport>) -> Unit): ListenerRegistration =
        firestore.collection("lostReports").whereEqualTo("ownerUid", ownerUid)
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    onUpdate(snapshot.documents.map { it.toLostReport() })
                }
            }

    fun getReport(reportId: String, onSuccess: (LostReport) -> Unit) {
        firestore.collection("lostReports").document(reportId).get()
            .addOnSuccessListener { onSuccess(it.toLostReport()) }
    }

    private fun DocumentSnapshot.toLostReport(): LostReport = LostReport(
        id = id,
        ownerUid = getString("ownerUid") ?: "",
        title = getString("title") ?: "",
        locationName = getString("locationName") ?: "",
        status = getString("status") ?: "",
        reportedAt = getTimestamp("reportedAt"),
        imageUrl = getString("imageUrl") ?: "",
        statusChangedAt = getTimestamp("statusChangedAt"),
        latitude = (get("latitude") as? Number)?.toDouble(),
        longitude = (get("longitude") as? Number)?.toDouble(),
        description = getString("description") ?: "",
        category = getString("category") ?: ""
    )
}
