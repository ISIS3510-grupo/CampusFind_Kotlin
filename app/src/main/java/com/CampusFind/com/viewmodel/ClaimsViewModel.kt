package com.CampusFind.com.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.CampusFind.com.data.ClaimsRepository
import com.CampusFind.com.model.ClaimReview

class ClaimsViewModel : ViewModel() {

    private val repository = ClaimsRepository()

    var claims by mutableStateOf(emptyList<ClaimReview>())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var inFlight = false

    fun load() {
        if (inFlight) {
            return
        }
        inFlight = true
        isLoading = true
        errorMessage = null

        repository.getPendingClaims(
            onSuccess = { result ->
                claims = result
                isLoading = false
                inFlight = false
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
                inFlight = false
            }
        )
    }
}
