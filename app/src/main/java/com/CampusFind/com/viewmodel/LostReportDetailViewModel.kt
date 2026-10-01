package com.CampusFind.com.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.CampusFind.com.model.LostReport
import com.google.firebase.firestore.FirebaseFirestore

class LostReportDetailViewModel : ViewModel() {

    private val firestore =
        FirebaseFirestore.getInstance()

    var report by mutableStateOf<LostReport?>(null)
        private set

    fun loadReport(
        reportId: String
    ) {
        if (reportId.isBlank()) {
            return
        }

        firestore
            .collection("lostReports")
            .document(reportId)
            .get()
            .addOnSuccessListener { document ->

                report =
                    LostReport(
                        id = document.id,
                        ownerUid =
                            document.getString("ownerUid")
                                ?: "",
                        title =
                            document.getString("title")
                                ?: "",
                        locationName =
                            document.getString("locationName")
                                ?: "",
                        status =
                            document.getString("status")
                                ?: "",
                        reportedAt =
                            document.getTimestamp("reportedAt"),
                        imageUrl =
                            document.getString("imageUrl")
                                ?: "",
                        statusChangedAt =
                            document.getTimestamp("statusChangedAt"),
                    )
            }
    }
}