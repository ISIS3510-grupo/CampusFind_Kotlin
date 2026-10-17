package com.CampusFind.com.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.CampusFind.com.data.ReportTimesRepository
import com.CampusFind.com.service.CategoryReportTime

class ReportTimesViewModel : ViewModel() {

    private val repository = ReportTimesRepository()

    var reportTimes by mutableStateOf(emptyList<CategoryReportTime>())
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

        repository.getReportTimes(
            onSuccess = { result ->
                reportTimes = result
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
