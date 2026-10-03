package com.CampusFind.com.data

import com.CampusFind.com.model.OfficeLocation
import com.google.firebase.firestore.FirebaseFirestore

class OfficeLocationsDataSource {

    private val firestore =
        FirebaseFirestore.getInstance()

    fun getActiveLocations(
        onSuccess: (List<OfficeLocation>) -> Unit,
        onError: (String) -> Unit
    ) {

        firestore
            .collection("officeLocations")
            .whereEqualTo("active", true)
            .get()
            .addOnSuccessListener { snapshot ->

                val locations =
                    snapshot.documents.mapNotNull { document ->

                        val latitude =
                            (document.get("latitude") as? Number)
                                ?.toDouble()
                                ?: return@mapNotNull null

                        val longitude =
                            (document.get("longitude") as? Number)
                                ?.toDouble()
                                ?: return@mapNotNull null

                        val radiusMeters =
                            (document.get("radiusMeters") as? Number)
                                ?.toDouble()
                                ?: 120.0

                        OfficeLocation(
                            id = document.id,
                            name =
                                document.getString("name")
                                    ?: "",
                            code =
                                document.getString("code")
                                    ?: "",
                            latitude = latitude,
                            longitude = longitude,
                            radiusMeters = radiusMeters,
                            active =
                                document.getBoolean("active")
                                    ?: true
                        )
                    }

                onSuccess(locations)
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.message
                        ?: "Error loading campus locations"
                )
            }
    }
}