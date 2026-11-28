
package com.CampusFind.com.model

data class AdminClaimCase(
    val id: String,
    val reportId: String,
    val foundItemId: String,
    val status: String,
    val reportTitle: String,
    val itemTitle: String,
    val claimantName: String,
    val claimantEmail: String,
    val ownershipAnswer: String?,
    val foundPrivateDetail: String?,
    val reportPrivateDetail: String?
)