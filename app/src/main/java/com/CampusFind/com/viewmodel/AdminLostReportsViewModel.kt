package com.CampusFind.com.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.CampusFind.com.data.AdminLostReportsRepository
import com.CampusFind.com.model.LostReport

class AdminLostReportsViewModel : ViewModel() {
    private val repository = AdminLostReportsRepository()

    var reports by mutableStateOf<List<LostReport>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadReports() {
        if (isLoading) return
        isLoading = true
        errorMessage = null
        repository.loadReports(
            onSuccess = {
                reports = it
                isLoading = false
            },
            onError = {
                errorMessage = it
                isLoading = false
            }
        )
    }

}
