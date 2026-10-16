package com.CampusFind.com.model

import com.CampusFind.com.service.ClaimVerdict

/** Datos de una revisión: referencias, textos privados, puntuación entre 0 y 1 y veredicto sugerido. */
data class ClaimReview(
    val claimId: String,
    val reportId: String,
    val foundItemId: String,
    val answer: String,
    val registered: String,
    val score: Double,
    val verdict: ClaimVerdict
)
