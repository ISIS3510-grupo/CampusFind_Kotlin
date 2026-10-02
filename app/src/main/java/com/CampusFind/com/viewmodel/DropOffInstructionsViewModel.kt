package com.CampusFind.com.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.CampusFind.com.data.OfficeLocationRepository
import com.CampusFind.com.model.OfficeLocation

class DropOffInstructionsViewModel : ViewModel() {

    private val repository =
        OfficeLocationRepository()

    var office by mutableStateOf<OfficeLocation?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadOffice() {

        if (isLoading) {
            return
        }

        isLoading = true
        errorMessage = null

        repository.getOfficeLocation(

            officeId = "ml",

            onSuccess = { result ->

                office = result
                isLoading = false

                if (result == null) {
                    errorMessage =
                        "Office information is not available yet."
                }
            },

            onError = { error ->

                isLoading = false
                errorMessage = error
            }
        )
    }
}