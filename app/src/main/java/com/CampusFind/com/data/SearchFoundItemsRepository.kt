
package com.CampusFind.com.data

import android.content.Context
import com.CampusFind.com.model.SearchFoundItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchFoundItemsRepository(
    context: Context
) {
    private val appContext = context.applicationContext
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    // Read previously saved items from the device
    suspend fun loadCachedItems(
        userId: String
    ): Pair<List<SearchFoundItem>, Long> =
        withContext(Dispatchers.IO) {
            val store = FoundItemsLocalStore(appContext, userId)

            Pair(
                store.loadItems(),
                store.getLastUpdated()
            )
        }

    // Persist the latest server response locally
    suspend fun saveCachedItems(
        userId: String,
        items: List<SearchFoundItem>
    ) = withContext(Dispatchers.IO) {
        FoundItemsLocalStore(appContext, userId)
            .saveItems(items)
    }

    // Fetch current available items from Firestore
    fun fetchAvailableItems(
        onSuccess: (List<SearchFoundItem>) -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("foundItems")
            .whereEqualTo("status", "available")
            .get(Source.SERVER)
            .addOnSuccessListener { snapshot ->

                val items = snapshot.documents.map { document ->
                    SearchFoundItem(
                        id = document.id,
                        title = document.getString("title") ?: "",
                        category = document.getString("category") ?: "",
                        locationName = document.getString("locationName") ?: "",
                        publicDescription =
                            document.getString("publicDescription") ?: "",
                        photoPath = document.getString("photoPath"),
                        createdAtMillis =
                            document.getTimestamp("createdAt")
                                ?.toDate()?.time
                    )
                }.sortedByDescending {
                    it.createdAtMillis ?: 0L
                }

                onSuccess(items)
            }
            .addOnFailureListener { exception ->
                onError(
                    exception.localizedMessage
                        ?: "Unable to load found items."
                )
            }
    }
}
