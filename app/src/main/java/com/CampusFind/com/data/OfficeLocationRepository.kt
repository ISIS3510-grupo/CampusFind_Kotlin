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

                if (
                    latitude == null ||
                    longitude == null
                ) {

                    onError(
                        "Office coordinates are not available."
                    )

                    return@addOnSuccessListener
                }

                val radiusMeters =
                    (document.get("radiusMeters") as? Number)
                        ?.toDouble()
                        ?: 120.0

                val office =
                    OfficeLocation(
                        id = document.id,

                        name =
                            document.getString("name")
                                ?: "Lost & Found Office",

                        code =
                            document.getString("code")
                                ?: "",

                        address =
                            document.getString("address")
                                ?: "",

                        latitude = latitude,

                        longitude = longitude,

                        radiusMeters =
                            radiusMeters,

                        active =
                            document.getBoolean("active")
                                ?: true
                    )

                onSuccess(
                    office
                )
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.localizedMessage
                        ?: "Unable to load office information."
                )
            }
    }
}