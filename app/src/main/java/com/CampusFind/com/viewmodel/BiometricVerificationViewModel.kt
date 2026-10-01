package com.CampusFind.com.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.CampusFind.com.service.BiometricAuthService
import com.CampusFind.com.service.BiometricAvailability

class BiometricVerificationViewModel :
    ViewModel() {

    var availability by
    mutableStateOf<BiometricAvailability?>(null)
        private set

    var isAuthenticating by
    mutableStateOf(false)
        private set

    var isVerified by
    mutableStateOf(false)
        private set

    var message by
    mutableStateOf<String?>(null)
        private set

    fun checkAvailability(
        service: BiometricAuthService
    ) {

        availability =
            service.getAvailability()

        message =
            when (availability) {

                BiometricAvailability.AVAILABLE ->
                    null

                BiometricAvailability.NO_HARDWARE ->
                    "This device does not have biometric hardware."

                BiometricAvailability.NOT_ENROLLED ->
                    "No fingerprint or face recognition is enrolled on this device."

                BiometricAvailability.UNAVAILABLE ->
                    "Biometric authentication is currently unavailable."

                null ->
                    null
            }
    }

    fun verify(
        service: BiometricAuthService
    ) {

        if (
            isAuthenticating ||
            isVerified
        ) {
            return
        }

        isAuthenticating = true
        message = null

        service.authenticate(

            onSuccess = {

                isAuthenticating = false
                isVerified = true

                message =
                    "Your identity was verified successfully."
            },

            onError = { error ->

                isAuthenticating = false
                isVerified = false
                message = error
            }
        )
    }
}