package com.CampusFind.com.data

import com.CampusFind.com.model.OfficeLocation
import com.google.firebase.firestore.FirebaseFirestore

class OfficeLocationRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    fun getOfficeLocation(
        officeId: String = "ml",
        onSuccess: (OfficeLocation?) -> Unit,
        onError: (String) -> Unit
    ) {

        firestore
            .collection("officeLocations")
            .document(officeId)
            .get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {
                    onSuccess(null)
                    return@addOnSuccessListener
                }

                val latitude =
                    (document.get("latitude") as? Number)
                        ?.toDouble()

                val longitude =
                    (document.get("longitude") as? Number)
                        ?.toDouble()

                val office =
                    OfficeLocation(
                        id = document.id,

                        name =
                            document.getString("name")
                                ?: "Lost & Found Office",

                        address =
                            document.getString("address")
                                ?: "",

                        latitude = latitude,

                        longitude = longitude,

                        active =
                            document.getBoolean("active")
                                ?: true
                    )

                onSuccess(office)
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.localizedMessage
                        ?: "Unable to load office information."
                )
            }
    }
}