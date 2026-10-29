package com.CampusFind.com.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.CampusFind.com.data.LostReportRepository
import com.CampusFind.com.data.OfficeLocationRepository

class ReportLostItemViewModel : ViewModel() {

    private val repository =
        LostReportRepository()

    var categories by
    mutableStateOf<List<String>>(emptyList())
        private set

    var isLoadingCategories by
    mutableStateOf(false)
        private set

    var errorMessage by
    mutableStateOf<String?>(null)
        private set

    private val locationRepository = OfficeLocationRepository()

    var locations by mutableStateOf<List<String>>(emptyList())
        private set

    fun loadLocations() {
        locationRepository.getActiveLocations(
            onSuccess = { result ->
                locations = result
                    .map { it.name.trim() }
                    .filter { it.isNotBlank() }
                    .distinct()
            },
            onError = { error ->
                errorMessage = error
            }
        )
    }
    fun loadCategories() {

        isLoadingCategories = true
        errorMessage = null

        repository.getCategories(

            onSuccess = { result ->

                categories = result
                isLoadingCategories = false
            },

            onError = { error ->

                categories = listOf("other")
                errorMessage = error
                isLoadingCategories = false
            }
        )
    }
}