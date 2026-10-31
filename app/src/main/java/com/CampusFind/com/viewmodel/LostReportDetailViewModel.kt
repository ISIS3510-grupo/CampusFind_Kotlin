package com.CampusFind.com.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.CampusFind.com.data.LostReportRepository
import com.CampusFind.com.model.LostReport

class LostReportDetailViewModel : ViewModel() {

    private val repository = LostReportRepository()

    var report by mutableStateOf<LostReport?>(null)
        private set

    fun loadReport(
        reportId: String
    ) {
        if (reportId.isBlank()) {
            return
        }

        repository.getReport(reportId) { report = it }
    }
}
