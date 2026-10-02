package com.CampusFind.com.data

import com.CampusFind.com.model.AppNotification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class NotificationsDataSource {

    private val firestore =
        FirebaseFirestore.getInstance()

    private val auth =
        FirebaseAuth.getInstance()

    fun observeNotifications(
        onUpdate: (List<AppNotification>) -> Unit,
        onError: (String) -> Unit
    ): ListenerRegistration? {

        val currentUser =
            auth.currentUser

        if (currentUser == null) {
            onError("User is not authenticated")
            return null
        }

        val uid =
            currentUser.uid

        return firestore
            .collection("notifications")
            .whereEqualTo(
                "recipientUid",
                uid
            )
            .orderBy(
                "sentAt",
                Query.Direction.DESCENDING
            )
            .addSnapshotListener { snapshot, exception ->

                if (exception != null) {

                    onError(
                        exception.message
                            ?: "Error loading notifications"
                    )

                    return@addSnapshotListener
                }

                val notifications =
                    snapshot
                        ?.documents
                        ?.map { document ->

                            AppNotification(
                                id =
                                    document.id,

                                recipientUid =
                                    document.getString(
                                        "recipientUid"
                                    ) ?: "",

                                matchId =
                                    document.getString(
                                        "matchId"
                                    ) ?: "",

                                channel =
                                    document.getString(
                                        "channel"
                                    ) ?: "",

                                sentAt =
                                    document.getTimestamp(
                                        "sentAt"
                                    ),

                                viewedAt =
                                    document.getTimestamp(
                                        "viewedAt"
                                    )
                            )
                        }
                        ?.filter {
                            it.channel == "in_app"
                        }
                        ?: emptyList()

                onUpdate(
                    notifications
                )
            }
    }
}