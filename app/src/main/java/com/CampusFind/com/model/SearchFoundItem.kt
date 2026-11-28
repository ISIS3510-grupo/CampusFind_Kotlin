package com.CampusFind.com.model

data class SearchFoundItem(
    val id: String = "",
    val title: String = "",
    val category: String = "",
    val locationName: String = "",
    val publicDescription: String = "",
    val photoPath: String? = null,
    val createdAtMillis: Long? = null
)
