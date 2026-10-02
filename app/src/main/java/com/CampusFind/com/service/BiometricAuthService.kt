package com.CampusFind.com.service

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

enum class BiometricAvailability {
    AVAILABLE,
    NO_HARDWARE,
    NOT_ENROLLED,
    UNAVAILABLE
}

class BiometricAuthService(
    private val activity: FragmentActivity
) {

    private val authenticators =
        BiometricManager.Authenticators.BIOMETRIC_WEAK

    fun getAvailability(): BiometricAvailability {

        val biometricManager =
            BiometricManager.from(activity)

        return when (
            biometricManager.canAuthenticate(
                authenticators
            )
        ) {

            BiometricManager.BIOMETRIC_SUCCESS ->
                BiometricAvailability.AVAILABLE

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                BiometricAvailability.NO_HARDWARE

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                BiometricAvailability.NOT_ENROLLED

            else ->
                BiometricAvailability.UNAVAILABLE
        }
    }

    fun authenticate(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        val executor =
            ContextCompat.getMainExecutor(
                activity
            )

        val biometricPrompt =
            BiometricPrompt(
                activity,
                executor,
                object :
                    BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationSucceeded(
                        result:
                        BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(
                            result
                        )

                        onSuccess()
                    }

                    override fun onAuthenticationFailed() {

                        super.onAuthenticationFailed()

                        onError(
                            "Biometric authentication was not recognized."
                        )
                    }

                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {

                        super.onAuthenticationError(
                            errorCode,
                            errString
                        )

                        onError(
                            errString.toString()
                        )
                    }
                }
            )

        val promptInfo =
            BiometricPrompt.PromptInfo
                .Builder()
                .setTitle(
                    "Verify your identity"
                )
                .setSubtitle(
                    "Use fingerprint or face recognition to continue with the claim."
                )
                .setAllowedAuthenticators(
                    authenticators
                )
                .setNegativeButtonText(
                    "Cancel"
                )
                .build()

        biometricPrompt.authenticate(
            promptInfo
        )
    }
}