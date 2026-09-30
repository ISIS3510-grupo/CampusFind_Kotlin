package com.CampusFind.com.model

data class PossibleMatch(
    val id: String = "",
    val lostReportId: String = "",
    val foundItemId: String = "",
    val score: Double = 0.0,
    val status: String = ""
)