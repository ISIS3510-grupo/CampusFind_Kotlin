package com.CampusFind.com.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.CampusFind.com.data.DonationPotentialRepository
import com.CampusFind.com.model.DonationCategoryResult

class DonationPotentialViewModel : ViewModel() {

    private val repository = DonationPotentialRepository()

    var results by mutableStateOf<List<DonationCategoryResult>>(emptyList())
        private set

    var semesterId by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadDonationPotential() {
        isLoading = true
        errorMessage = null

        repository.loadDonationPotential(
            onSemester = { semesterId = it },
            onSuccess = {
                results = it
                isLoading = false
            },
            onError = {
                errorMessage = it
                isLoading = false
            }
        )
    }
}
