package com.CampusFind.com.model

import com.google.firebase.Timestamp

data class FoundItemCandidate(
    val id: String = "",
    val category: String = "",
    val title: String = "",
    val publicDescription: String = "",
    val status: String = "",
    val locationName: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val createdAt: Timestamp? = null
)