package com.CampusFind.com.data

import com.CampusFind.com.service.CategoryReportTime
import com.CampusFind.com.service.ReportTime
import com.CampusFind.com.service.ReportTimeCalculator
import com.google.firebase.firestore.FirebaseFirestore

class ReportTimesRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun getReportTimes(
        onSuccess: (List<CategoryReportTime>) -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("lostReports")
            .get()
            .addOnSuccessListener { snapshot ->
                val reports = mutableListOf<ReportTime>()
                for (document in snapshot.documents) {
                    val category = document.getString("category") ?: continue
                    val reportedAt = document.getTimestamp("reportedAt") ?: continue
                    val foundAt = document.getTimestamp("foundAt")
                    reports.add(
                        ReportTime(
                            category = category,
                            reportedAt = reportedAt.toDate().time,
                            foundAt = foundAt?.toDate()?.time
                        )
                    )
                }
                onSuccess(ReportTimeCalculator.calculate(reports))
            }
            .addOnFailureListener { exception ->
                onError(exception.localizedMessage ?: "Unable to load report times.")
            }
    }
}
