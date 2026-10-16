package com.CampusFind.com.model

import com.google.firebase.Timestamp

data class LostReport(
    val id: String = "",
    val ownerUid: String = "",
    val title: String = "",
    val locationName: String = "",
    val status: String = "",
    val reportedAt: Timestamp? = null,
    val imageUrl: String = "",
    val statusChangedAt: Timestamp? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val category: String = "",
    val description: String = ""
)