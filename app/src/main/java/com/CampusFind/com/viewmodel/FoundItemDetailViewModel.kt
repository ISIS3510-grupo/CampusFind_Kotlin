package com.CampusFind.com.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

data class FoundItemDetail(
    val id: String = "",
    val title: String = "",
    val locationName: String = "",
    val createdAt: Timestamp? = null
)

class FoundItemDetailViewModel : ViewModel() {

    private val firestore =
        FirebaseFirestore.getInstance()

    var foundItem by
    mutableStateOf<FoundItemDetail?>(null)
        private set

    fun loadFoundItem(
        foundItemId: String
    ) {
        if (foundItemId.isBlank()) {
            foundItem = null
            return
        }

        firestore
            .collection("foundItems")
            .document(foundItemId)
            .get()
            .addOnSuccessListener { document ->

                foundItem =
                    FoundItemDetail(
                        id = document.id,
                        title =
                            document.getString("title")
                                ?: "",
                        locationName =
                            document.getString("locationName")
                                ?: "",
                        createdAt =
                            document.getTimestamp("createdAt")
                    )
            }
    }
}