package com.CampusFind.com.model

data class OfficeLocation(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val active: Boolean = true
) {

    val hasCoordinates: Boolean
        get() =
            latitude != null &&
                    longitude != null &&
                    latitude in -90.0..90.0 &&
                    longitude in -180.0..180.0
}