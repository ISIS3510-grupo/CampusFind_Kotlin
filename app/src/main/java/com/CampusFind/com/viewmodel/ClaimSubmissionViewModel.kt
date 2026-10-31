package com.CampusFind.com.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.CampusFind.com.data.ClaimSubmissionRepository

class ClaimSubmissionViewModel : ViewModel() {
    private val repository = ClaimSubmissionRepository()

    var ownershipAnswer by mutableStateOf("")
        private set
    var isSending by mutableStateOf(false)
        private set
    var isSubmitted by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun updateOwnershipAnswer(answer: String) {
        if (isSending || isSubmitted) return
        ownershipAnswer = answer
        errorMessage = null
    }

    fun submitClaim(matchId: String) {
        if (isSending || isSubmitted || ownershipAnswer.isBlank()) return
        isSending = true
        errorMessage = null
        repository.submitClaim(
            matchId = matchId,
            ownershipAnswer = ownershipAnswer,
            onSuccess = {
                isSending = false
                isSubmitted = true
            },
            onError = {
                isSending = false
                errorMessage = it
            }
        )
    }
}
