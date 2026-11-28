package com.CampusFind.com.model

data class AdminDashboardMetrics(
    val newFound: Long,
    val matches: Long,
    val claims: Long
)

data class AdminPendingClaim(
    val id: String,
    val itemTitle: String,
    val studentName: String,
    val createdAtMillis: Long
)
