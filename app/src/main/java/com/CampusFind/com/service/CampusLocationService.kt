package com.CampusFind.com.service

import android.location.Location
import com.CampusFind.com.model.LocationData
import com.CampusFind.com.model.OfficeLocation
import java.util.Locale

class CampusLocationService {

    fun getSuggestedLocation(
        currentLocation: LocationData,
        officeLocations: List<OfficeLocation>
    ): String {

        val nearestLocation =
            officeLocations
                .map { officeLocation ->

                    officeLocation to calculateDistance(
                        currentLocation = currentLocation,
                        officeLocation = officeLocation
                    )
                }
                .filter { (officeLocation, distance) ->

                    distance <= officeLocation.radiusMeters
                }
                .minByOrNull { (_, distance) ->

                    distance
                }

        if (nearestLocation != null) {
            return nearestLocation.first.name
        }

        return coordinatesAsText(currentLocation)
    }

    fun coordinatesAsText(
        location: LocationData
    ): String {

        return String.format(
            Locale.US,
            "%.5f, %.5f",
            location.latitude,
            location.longitude
        )
    }

    private fun calculateDistance(
        currentLocation: LocationData,
        officeLocation: OfficeLocation
    ): Float {

        val result = FloatArray(1)

        Location.distanceBetween(
            currentLocation.latitude,
            currentLocation.longitude,
            officeLocation.latitude,
            officeLocation.longitude,
            result
        )

        return result[0]
    }
}