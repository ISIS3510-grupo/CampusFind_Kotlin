package com.CampusFind.com.data

import com.google.firebase.functions.FirebaseFunctions

class AdminWalkInRepository {

    private val functions =
        FirebaseFunctions.getInstance("us-east1")

    fun deliver(
        itemId: String,
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val data = mapOf(
            "foundItemId" to itemId,
            "studentEmail" to email.trim().lowercase(),
            "physicalIdVerified" to true,
            "ownershipVerified" to true
        )

        functions.getHttpsCallable("adminDeliverWalkIn")
            .call(data)
            .addOnSuccessListener { result ->
                val response = result.data as? Map<*, *>

                if (response?.get("status") == "delivered") {
                    AdminFoundItemDetailCache.invalidate(itemId)
                    onSuccess()
                } else {
                    onError("Delivery was not confirmed.")
                }
            }
            .addOnFailureListener { error ->
                onError(
                    error.localizedMessage
                        ?: "Unable to register delivery."
                )
            }
    }
}
