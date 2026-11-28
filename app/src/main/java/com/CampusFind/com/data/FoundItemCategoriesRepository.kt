package com.CampusFind.com.data

import com.CampusFind.com.service.CategoryFoundCount
import com.CampusFind.com.service.FoundItemCategoryCounter
import com.google.firebase.firestore.FirebaseFirestore

class FoundItemCategoriesRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun getCategoryCounts(
        onSuccess: (List<CategoryFoundCount>) -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("foundItems")
            .get()
            .addOnSuccessListener { snapshot ->
                val categories = mutableListOf<String>()
                for (document in snapshot.documents) {
                    val category = document.getString("category") ?: continue
                    categories.add(category)
                }
                onSuccess(FoundItemCategoryCounter.calculate(categories))
            }
            .addOnFailureListener { exception ->
                onError(exception.localizedMessage ?: "Unable to load found item categories.")
            }
    }
}
