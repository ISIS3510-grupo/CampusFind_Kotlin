package com.CampusFind.com.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.CampusFind.com.data.AdminClaimReviewRepository
import com.CampusFind.com.model.AdminClaimCase

class AdminClaimReviewViewModel : ViewModel() {

    private val repository = AdminClaimReviewRepository()
    private var version = 0

    var claims by mutableStateOf<List<AdminClaimCase>>(emptyList())
        private set

    var selectedId by mutableStateOf<String?>(null)
        private set

    var loading by mutableStateOf(false)
        private set

    var busy by mutableStateOf(false)
        private set

    var delivered by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    val selectedClaim: AdminClaimCase?
        get() = claims.firstOrNull { it.id == selectedId }

    fun clear() {
        ++version
        claims = emptyList()
        selectedId = null
        delivered = false
        loading = false
        busy = false
        error = null
    }

    fun loadClaims(reportId: String?, itemId: String?) {
        val request = ++version
        loading = true
        error = null
        claims = emptyList()
        selectedId = null
        delivered = false

        repository.loadClaims(
            reportId, itemId,
            success = { result ->
                if (request == version) {
                    claims = result
                    selectedId = result.firstOrNull()?.id
                    delivered = selectedClaim?.status == "delivered"
                    loading = false
                }
            },
            error = { message ->
                if (request == version) {
                    error = message
                    loading = false
                }
            }
        )
    }

    fun selectClaim(id: String) {
        if (claims.any { it.id == id }) {
            selectedId = id
            delivered = selectedClaim?.status == "delivered"
        }
    }

    private fun execute(
        operation: (() -> Unit, (String) -> Unit) -> Unit,
        after: () -> Unit
    ) {
        if (busy || loading) return

        val request = ++version
        busy = true
        error = null

        operation(
            {
                if (request == version) {
                    busy = false
                    after()
                }
            },
            {
                if (request == version) {
                    error = it
                    busy = false
                }
            }
        )
    }

    fun review(
        approve: Boolean,
        verified: Boolean,
        reportId: String?,
        itemId: String?
    ) {
        val claim = selectedClaim ?: return

        if (claim.status != "pending" ||
            (approve && !verified)
        ) return

        execute(
            { ok, fail ->
                repository.review(claim, approve, verified, ok, fail)
            },
            { loadClaims(reportId, itemId) }
        )
    }

    fun deliver(
        verified: Boolean,
        identityChecked: Boolean,
        reportId: String?,
        itemId: String?
    ) {
        val claim = selectedClaim ?: return

        if (claim.status !in listOf("pending", "approved") ||
            !verified || !identityChecked
        ) return

        execute(
            { ok, fail -> repository.deliver(claim, ok, fail) },
            { loadClaims(reportId, itemId) }
        )
    }

    fun close(
        reportId: String,
        itemId: String,
        onFinished: () -> Unit
    ) {
        if (!delivered) return

        execute(
            { ok, fail ->
                repository.close(reportId, itemId, ok, fail)
            },
            onFinished
        )
    }

    fun offline() {
        clear()
        error = "Claims and private details are unavailable offline."
    }

    override fun onCleared() {
        clear()
        super.onCleared()
    }
}

