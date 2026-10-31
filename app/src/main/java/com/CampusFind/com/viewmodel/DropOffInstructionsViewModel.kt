package com.CampusFind.com.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.CampusFind.com.data.OfficeLocationCache
import com.CampusFind.com.data.OfficeLocationRepository
import com.CampusFind.com.model.OfficeLocation

class DropOffInstructionsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        OfficeLocationRepository(
            OfficeLocationCache(application)
        )

    var office by mutableStateOf<OfficeLocation?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadOffice() {

        if (isLoading) return

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