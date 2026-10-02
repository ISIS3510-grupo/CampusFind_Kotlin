package com.CampusFind.com.service

interface MapService {

    fun openDirections(
        latitude: Double,
        longitude: Double
    ): Boolean
}