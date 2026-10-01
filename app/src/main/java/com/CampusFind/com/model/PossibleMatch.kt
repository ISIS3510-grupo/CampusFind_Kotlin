package com.CampusFind.com.model

import com.google.firebase.Timestamp

data class PossibleMatch(
    val id: String = "",
    val reportId: String = "",
    val foundItemId: String = "",
    val ownerUid: String = "",
    val score: Double = 0.0,
    val strategyName: String = "",
    val strategyVersion: String = "",
    val createdAt: Timestamp? = null
)