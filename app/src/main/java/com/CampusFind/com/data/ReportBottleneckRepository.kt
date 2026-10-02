package com.CampusFind.com.data

import com.CampusFind.com.service.BottleneckReport
import com.google.firebase.firestore.FirebaseFirestore

class ReportBottleneckRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    fun getReports(
        onSuccess: (List<BottleneckReport>) -> Unit,
        onError: (String) -> Unit
    ) {

        firestore
            .collection("lostReports")
            .get()
            .addOnSuccessListener { snapshot ->

                val reports =
                    snapshot.documents
                        .mapNotNull { document ->

                            val status =
                                document.getString(
                                    "status"
                                )
                                    ?: return@mapNotNull null

                            BottleneckReport(
                                id =
                                    document.id,

                                status =
                                    status,

                                statusChangedAt =
                                    document
                                        .getTimestamp(
                                            "statusChangedAt"
                                        )
                                        ?.toDate()
                                        ?.time
                            )
                        }

                onSuccess(reports)
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.localizedMessage
                        ?: "Unable to load report bottleneck data."
                )
            }
    }
}