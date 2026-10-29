package com.CampusFind.com.model

import com.google.firebase.Timestamp

data class FoundItemDetail(
    val id: String = "",
    val title: String = "",
    val locationName: String = "",
    val createdAt: Timestamp? = null
)
