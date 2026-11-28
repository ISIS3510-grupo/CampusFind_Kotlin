
package com.CampusFind.com.data

import com.CampusFind.com.model.FoundItemDetail
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

class AdminFoundInventoryRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun loadItems(
        onSuccess: (List<FoundItemDetail>) -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("foundItems")
            .get(Source.SERVER)
            .addOnSuccessListener { snapshot ->

                val foundItems = snapshot.documents.map { document ->
                    FoundItemDetail(
                        id = document.id,
                        title = document.getString("title") ?: "",
                        locationName = document.getString("locationName") ?: "",
                        createdAt = document.getTimestamp("createdAt"),
                        status = document.getString("status") ?: "",
                        category = document.getString("category") ?: ""
                    )
                }.sortedByDescending {
                    it.createdAt?.toDate()?.time ?: 0L
                }

                onSuccess(foundItems)
            }
            .addOnFailureListener { exception ->
                onError(
                    exception.localizedMessage
                        ?: "Unable to load found inventory."
                )
            }
    }
}
