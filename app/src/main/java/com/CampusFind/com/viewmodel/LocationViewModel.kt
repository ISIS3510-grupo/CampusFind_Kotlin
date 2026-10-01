package com.CampusFind.com.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.CampusFind.com.data.OfficeLocationsDataSource
import com.CampusFind.com.model.LocationData
import com.CampusFind.com.model.LocationPermissionStatus
import com.CampusFind.com.service.CampusLocationService
import com.CampusFind.com.service.LocationService

class LocationViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val locationService =
        LocationService(application)

    private val officeLocationsDataSource =
        OfficeLocationsDataSource()

    private val campusLocationService =
        CampusLocationService()

    var location by
    mutableStateOf<LocationData?>(null)
        private set

    var suggestedCampusLocation by
    mutableStateOf("")
        private set

    var permissionStatus by
    mutableStateOf<LocationPermissionStatus?>(null)
        private set

    var isLoading by
    mutableStateOf(false)
        private set

    var errorMessage by
    mutableStateOf<String?>(null)
        private set

    fun onPermissionResult(
        status: LocationPermissionStatus
    ) {

        permissionStatus = status
        errorMessage = null

        when (status) {

            LocationPermissionStatus.GRANTED -> {
                getCurrentLocation()
            }

            LocationPermissionStatus.DENIED -> {
                errorMessage =
                    "Location permission denied"
            }

            LocationPermissionStatus.PERMANENTLY_DENIED -> {
                errorMessage =
                    "Location permission permanently denied"
            }
        }
    }

    fun getCurrentLocation() {

        if (!locationService.isLocationEnabled()) {

            errorMessage =
                "Location services are disabled"

            return
        }

        isLoading = true
        errorMessage = null

        locationService.getCurrentLocation(

            onSuccess = { result ->

                location = result

                // Mostramos coordenadas mientras
                // obtenemos el contexto de Firestore.
                suggestedCampusLocation =
                    campusLocationService
                        .coordinatesAsText(result)

                loadCampusLocation(result)
            },

            onError = { error ->

                errorMessage = error
                isLoading = false
            }
        )
    }

    private fun loadCampusLocation(
        currentLocation: LocationData
    ) {

        officeLocationsDataSource
            .getActiveLocations(

                onSuccess = { officeLocations ->

                    suggestedCampusLocation =
                        campusLocationService
                            .getSuggestedLocation(
                                currentLocation =
                                    currentLocation,
                                officeLocations =
                                    officeLocations
                            )

                    isLoading = false
                },

                onError = {

                    // Si Firestore no puede consultarse,
                    // conservamos las coordenadas obtenidas
                    // por GPS en vez de romper el formulario.
                    suggestedCampusLocation =
                        campusLocationService
                            .coordinatesAsText(
                                currentLocation
                            )

                    isLoading = false
                }
            )
    }
}