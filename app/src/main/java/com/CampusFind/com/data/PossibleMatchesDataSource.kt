package com.CampusFind.com.data

import com.CampusFind.com.model.PossibleMatch
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class PossibleMatchesDataSource {

    private val firestore = FirebaseFirestore.getInstance()

    fun observePossibleMatches(
        lostReportId: String,
        onUpdate: (List<PossibleMatch>) -> Unit,
        onError: (String) -> Unit
    ): ListenerRegistration {

        return firestore.collection("matches")
            .whereEqualTo("lostReportId", lostReportId)
            .whereEqualTo("status", "suggested")
            .addSnapshotListener { snapshot, exception ->

                if (exception != null) {
                    onError(exception.message ?: "Error loading possible matches")
                    return@addSnapshotListener
                }

                val matches = snapshot?.documents?.map { document ->
                    PossibleMatch(
                        id = document.id,
                        lostReportId = document.getString("lostReportId") ?: "",
                        foundItemId = document.getString("foundItemId") ?: "",
                        score = document.getDouble("score") ?: 0.0,
                        status = document.getString("status") ?: ""
                    )
                } ?: emptyList()

                onUpdate(matches)
            }
    }
}