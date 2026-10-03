package com.CampusFind.com.model

data class OfficeLocation(
    val id: String = "",
    val name: String = "",
    val code: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val radiusMeters: Double = 120.0,
    val active: Boolean = true
) {

    val hasCoordinates: Boolean
        get() =
            latitude.isFinite() &&
                    longitude.isFinite() &&
                    latitude in -90.0..90.0 &&
                    longitude in -180.0..180.0
}
