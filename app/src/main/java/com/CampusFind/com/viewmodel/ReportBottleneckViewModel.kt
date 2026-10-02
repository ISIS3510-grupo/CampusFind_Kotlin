package com.CampusFind.com.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.CampusFind.com.data.ReportBottleneckRepository
import com.CampusFind.com.service.ReportBottleneckCalculator
import com.CampusFind.com.service.ReportBottleneckSummary

class ReportBottleneckViewModel : ViewModel() {

    private val repository =
        ReportBottleneckRepository()

    var summary by
    mutableStateOf<ReportBottleneckSummary?>(null)
        private set

    var isLoading by
    mutableStateOf(false)
        private set

    var errorMessage by
    mutableStateOf<String?>(null)
        private set

    fun load() {

        if (isLoading) {
            return
        }

        isLoading = true
        errorMessage = null

        repository.getReports(

            onSuccess = { reports ->

                summary =
                    ReportBottleneckCalculator
                        .calculate(reports)

                isLoading = false
            },

            onError = { error ->

                errorMessage = error
                isLoading = false
            }
        )
    }
}