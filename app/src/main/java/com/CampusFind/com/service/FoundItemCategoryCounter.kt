package com.CampusFind.com.service

data class CategoryFoundCount(
    val category: String,
    val count: Int,
    val percentage: Double
)

object FoundItemCategoryCounter {

    fun calculate(categories: List<String>): List<CategoryFoundCount> {
        if (categories.isEmpty()) {
            return emptyList()
        }
        val normalized = categories.map { category ->
            category.trim().lowercase().ifEmpty { "uncategorized" }
        }
        return normalized.groupBy { it }.map { (category, items) ->
            CategoryFoundCount(category, items.size, items.size * 100.0 / normalized.size)
        }.sortedWith(compareByDescending<CategoryFoundCount> { it.count }.thenBy { it.category })
    }
}
