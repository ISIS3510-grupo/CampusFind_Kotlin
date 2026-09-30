package com.CampusFind.com.model

data class OfficeLocation(
    val id: String = "",
    val name: String = "",
    val code: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val radiusMeters: Double = 120.0,
    val active: Boolean = true
)
