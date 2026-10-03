package com.CampusFind.com.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.CampusFind.com.service.AuthenticationService

class AuthenticationViewModel : ViewModel() {

    private val authenticationService =
        AuthenticationService()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isAuthenticated by mutableStateOf(
        authenticationService.isStudentAuthenticated()
    )
        private set

    fun loginStudent(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        if (email.isBlank()) {
            errorMessage = "Enter your Uniandes email."
            return
        }

        if (password.isBlank()) {
            errorMessage = "Enter your password."
            return
        }

        isLoading = true
        errorMessage = null

        authenticationService.loginStudent(
            email = email,
            password = password,

            onSuccess = {
                isLoading = false
                isAuthenticated = true
                errorMessage = null
                onSuccess()
            },

            onError = { error ->
                isLoading = false
                isAuthenticated = false
                errorMessage = error
            }
        )
    }

    fun loginAdmin(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        if (email.isBlank()) {
            errorMessage = "Enter your Uniandes email."
            return
        }

        if (password.isBlank()) {
            errorMessage = "Enter your password."
            return
        }

        isLoading = true
        errorMessage = null

        authenticationService.loginAdmin(
            email = email,
            password = password,

            onSuccess = {
                isLoading = false
                isAuthenticated = true
                errorMessage = null
                onSuccess()
            },

            onError = { error ->
                isLoading = false
                isAuthenticated = false
                errorMessage = error
            }
        )
    }

    fun getCurrentUserEmail(): String {
        return authenticationService.getCurrentUserEmail()
    }

    fun clearError() {
        errorMessage = null
    }

    fun logout() {
        authenticationService.signOut()
        isAuthenticated = false
    }
    fun getCurrentUserUid(): String {
        return authenticationService.getCurrentUserUid()
    }
}