package com.CampusFind.com.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

data class MatchSearchTimeSummary(
    val sampleCount: Int = 0,
    val averageMs: Double? = null
)

class MatchSearchTimeRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    private val auth =
        FirebaseAuth.getInstance()

    fun recordDuration(
        durationMs: Long
    ) {

        val uid =
            auth.currentUser?.uid
                ?: return

        if (durationMs < 0) {
            return
        }

        val metric =
            hashMapOf<String, Any>(
                "uid" to uid,
                "metricType" to "match_search",
                "platform" to "kotlin",
                "durationMs" to durationMs,
                "recordedAt" to
                        FieldValue.serverTimestamp()
            )

        firestore
            .collection("performanceMetrics")
            .add(metric)
    }

    fun getSummary(
        onSuccess: (MatchSearchTimeSummary) -> Unit,
        onError: (String) -> Unit
    ) {

        firestore
            .collection("performanceMetrics")
            .whereEqualTo(
                "metricType",
                "match_search"
            )
            .get()
            .addOnSuccessListener { snapshot ->

                val durations =
                    snapshot.documents
                        .filter { document ->
                            document.getString(
                                "platform"
                            ) == "kotlin"
                        }
                        .mapNotNull { document ->

                            val duration =
                                (document.get(
                                    "durationMs"
                                ) as? Number)
                                    ?.toDouble()

                            duration
                                ?.takeIf {
                                    it.isFinite() &&
                                            it >= 0.0
                                }
                        }

                val summary =
                    if (durations.isEmpty()) {

                        MatchSearchTimeSummary()

                    } else {

                        MatchSearchTimeSummary(
                            sampleCount =
                                durations.size,

                            averageMs =
                                durations.average()
                        )
                    }

                onSuccess(summary)
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.localizedMessage
                        ?: "Unable to load match search times."
                )
            }
    }
}