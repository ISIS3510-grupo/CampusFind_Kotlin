package com.CampusFind.com.model

data class FoundItemDraft(
    val category: String,
    val description: String,
    val location: String,
    val time: String
) {
    fun isEmpty(): Boolean =
        category.isBlank() && description.isBlank() && location.isBlank() && time.isBlank()
}
