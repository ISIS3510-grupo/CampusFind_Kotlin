package com.CampusFind.com.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.CampusFind.com.model.LostReport
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class LostReportsViewModel : ViewModel() {

    private val firestore =
        FirebaseFirestore.getInstance()

    private var listener:
            ListenerRegistration? = null

    var reports by mutableStateOf<List<LostReport>>(emptyList())
        private set

    fun observeReports(
        ownerUid: String
    ) {
        if (ownerUid.isBlank()) {
            reports = emptyList()
            return
        }

        listener?.remove()

        listener =
            firestore
                .collection("lostReports")
                .whereEqualTo(
                    "ownerUid",
                    ownerUid
                )
                .addSnapshotListener { snapshot, error ->

                    if (error != null || snapshot == null) {
                        return@addSnapshotListener
                    }

                    reports =
                        snapshot.documents.map { document ->

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
                                        ?: ""
                            )
                        }
                }
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}
