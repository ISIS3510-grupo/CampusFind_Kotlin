package com.CampusFind.com.model

import com.CampusFind.com.service.ClaimVerdict

data class ClaimReview(
    val claimId: String,
    val reportId: String,
    val foundItemId: String,
    val answer: String,
    val registered: String,
    val score: Double,
    val verdict: ClaimVerdict
)
