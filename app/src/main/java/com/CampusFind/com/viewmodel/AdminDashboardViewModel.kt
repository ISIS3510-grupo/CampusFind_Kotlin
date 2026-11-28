package com.CampusFind.com.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.CampusFind.com.data.AdminDashboardRepository
import com.CampusFind.com.model.AdminDashboardMetrics
import com.CampusFind.com.model.AdminPendingClaim

class AdminDashboardViewModel : ViewModel() {

    private val repository = AdminDashboardRepository()
    private var metricsInFlight = false
    private var claimsInFlight = false

    var metrics by mutableStateOf<AdminDashboardMetrics?>(null)
        private set
    var pendingClaims by mutableStateOf<List<AdminPendingClaim>>(emptyList())
        private set
    var isLoadingMetrics by mutableStateOf(true)
        private set
    var isLoadingClaims by mutableStateOf(true)
        private set
    var metricsError by mutableStateOf<String?>(null)
        private set
    var claimsError by mutableStateOf<String?>(null)
        private set

    fun load() {
        loadMetrics()
        loadPendingClaims()
    }

    private fun loadMetrics() {
        if (metricsInFlight) return
        metricsInFlight = true
        isLoadingMetrics = true
        metricsError = null
        repository.getMetrics(
            onSuccess = { result ->
                metrics = result
                isLoadingMetrics = false
                metricsInFlight = false
            },
            onError = { error ->
                metricsError = error
                isLoadingMetrics = false
                metricsInFlight = false
            }
        )
    }

    private fun loadPendingClaims() {
        if (claimsInFlight) return
        claimsInFlight = true
        isLoadingClaims = true
        claimsError = null
        repository.getPendingClaims(
            onSuccess = { result ->
                pendingClaims = result
                isLoadingClaims = false
                claimsInFlight = false
            },
            onError = { error ->
                claimsError = error
                isLoadingClaims = false
                claimsInFlight = false
            }
        )
    }
}
