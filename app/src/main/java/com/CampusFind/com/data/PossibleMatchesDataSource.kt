package com.CampusFind.com.data

import com.CampusFind.com.model.PossibleMatch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class PossibleMatchesDataSource {

    private val firestore =
        FirebaseFirestore.getInstance()

    private val auth =
        FirebaseAuth.getInstance()

    fun observePossibleMatches(
        reportId: String,
        onUpdate: (List<PossibleMatch>) -> Unit,
        onError: (String) -> Unit
    ): ListenerRegistration? {

        val currentUser =
            auth.currentUser

        if (currentUser == null) {
            onError("User is not authenticated")
            return null
        }

        val ownerUid =
            currentUser.uid

        return firestore
            .collection("matches")
            .whereEqualTo(
                "ownerUid",
                ownerUid
            )
            .whereEqualTo(
                "reportId",
                reportId
            )
            .addSnapshotListener { snapshot, exception ->

                if (exception != null) {

                    onError(
                        exception.message
                            ?: "Error loading possible matches"
                    )

                    return@addSnapshotListener
                }

                val matches =
                    snapshot
                        ?.documents
                        ?.map { document ->

                            PossibleMatch(
                                id = document.id,

                                reportId =
                                    document.getString(
                                        "reportId"
                                    ) ?: "",

                                foundItemId =
                                    document.getString(
                                        "foundItemId"
                                    ) ?: "",

                                ownerUid =
                                    document.getString(
                                        "ownerUid"
                                    ) ?: "",

                                score =
                                    document.getDouble(
                                        "score"
                                    ) ?: 0.0,

                                strategyName =
                                    document.getString(
                                        "strategyName"
                                    ) ?: "",

                                strategyVersion =
                                    document.getString(
                                        "strategyVersion"
                                    ) ?: "",

                                createdAt =
                                    document.getTimestamp(
                                        "createdAt"
                                    )
                            )
                        }
                        ?: emptyList()

                onUpdate(matches)
            }
    }
}