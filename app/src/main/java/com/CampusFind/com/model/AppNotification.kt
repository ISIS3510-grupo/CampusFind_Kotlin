package com.CampusFind.com.model

import com.google.firebase.Timestamp

data class AppNotification(
    val id: String = "",
    val recipientUid: String = "",
    val matchId: String = "",
    val channel: String = "",
    val sentAt: Timestamp? = null,
    val viewedAt: Timestamp? = null
)
