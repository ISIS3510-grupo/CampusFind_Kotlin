package com.CampusFind.com.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.CampusFind.com.data.AdminWalkInRepository

class AdminWalkInViewModel : ViewModel() {

    private val repository = AdminWalkInRepository()

    var email by mutableStateOf("")
        private set

    var identityChecked by mutableStateOf(false)
        private set

    var isBusy by mutableStateOf(false)
        private set

    var delivered by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    val emailValid: Boolean
        get() = Regex(
            "^[^\\s@]+@uniandes\\.edu\\.co$",
            RegexOption.IGNORE_CASE
        ).matches(email.trim())

    fun updateEmail(value: String) {
        if (!email.equals(value, ignoreCase = true)) {
            identityChecked = false
        }
        email = value
        error = null
    }

    fun updateIdentityChecked(value: Boolean) {
        identityChecked = value
    }

    fun deliver(itemId: String) {
        if (isBusy || delivered ||
            !emailValid || !identityChecked ||
            itemId.isBlank()
        ) return

        isBusy = true
        error = null

        repository.deliver(
            itemId,
            email,
            onSuccess = {
                delivered = true
                isBusy = false
            },
            onError = {
                error = it
                isBusy = false
            }
        )
    }
}
