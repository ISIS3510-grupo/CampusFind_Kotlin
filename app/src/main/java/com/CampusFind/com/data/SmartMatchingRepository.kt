package com.CampusFind.com.data

import com.CampusFind.com.model.FoundItemCandidate
import com.CampusFind.com.model.LostReport
import com.CampusFind.com.model.MatchResult
import com.CampusFind.com.service.BasicMatchingStrategy
import com.CampusFind.com.service.MatchingService
import com.google.firebase.firestore.FirebaseFirestore

class SmartMatchingRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    private val settingsRepository =
        MatchingSettingsRepository()

    fun generateMatches(
        lostReport: LostReport,
        onSuccess: (List<MatchResult>) -> Unit,
        onError: (String) -> Unit
    ) {

        if (
            lostReport.id.isBlank() ||
            lostReport.ownerUid.isBlank()
        ) {
            onError(
                "Lost report information is incomplete."
            )
            return
        }

        settingsRepository
            .getMatchingThreshold(

                onSuccess = { threshold ->

                    loadFoundItems(
                        lostReport = lostReport,
                        threshold = threshold,
                        onSuccess = onSuccess,
                        onError = onError
                    )
                },

                onError = onError
            )
    }

    private fun loadFoundItems(
        lostReport: LostReport,
        threshold: Double,
        onSuccess: (List<MatchResult>) -> Unit,
        onError: (String) -> Unit
    ) {

        firestore
            .collection("foundItems")
            .whereEqualTo(
                "status",
                "available"
            )
            .get()
            .addOnSuccessListener { snapshot ->

                val foundItems =
                    snapshot.documents
                        .map { document ->

                            FoundItemCandidate(
                                id =
                                    document.id,

                                category =
                                    document.getString(
                                        "category") ?: "",

                                title =
                                    document.getString(
                                        "title") ?: "",

                                publicDescription =
                                    document.getString(
                                        "publicDescription") ?: "",

                                status =
                                    document.getString(
                                        "status") ?: "",

                                locationName =
                                    document.getString(
                                        "locationName") ?: "",

                                latitude =
                                    (document.get(
                                        "latitude") as? Number)
                                        ?.toDouble(),

                                longitude =
                                    (document.get(
                                        "longitude") as? Number)
                                        ?.toDouble(),

                                createdAt =
                                    document.getTimestamp(
                                        "createdAt")
                            )
                        }

                val matchingService =
                    MatchingService(
                        strategy =
                            BasicMatchingStrategy(),
                        threshold =
                            threshold
                    )

                val results =
                    matchingService
                        .findPossibleMatches(
                            lost = lostReport,
                            foundItems = foundItems
                        )

                onSuccess(results)
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.localizedMessage
                        ?: "Unable to load found items."
                )
            }
    }
}