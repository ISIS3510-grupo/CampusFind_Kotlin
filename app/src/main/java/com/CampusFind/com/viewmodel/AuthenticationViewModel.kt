package com.CampusFind.com.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.CampusFind.com.service.AuthenticationService

/** Expone carga, errores y autenticación como estado de Compose y coordina el servicio de acceso. */
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

    /** Valida campos obligatorios, activa la carga y actualiza el estado según el resultado del acceso de estudiante. */
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

    /** Valida campos obligatorios y coordina el acceso administrativo; invoca onSuccess después de validar el rol. */
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

    /** Obtiene del servicio el correo de la sesión actual para mostrarlo en la interfaz. */
    fun getCurrentUserEmail(): String {
        return authenticationService.getCurrentUserEmail()
    }

    /** Borra el mensaje de error visible al editar campos o volver a abrir el diálogo. */
    fun clearError() {
        errorMessage = null
    }

    /** Cierra la sesión mediante el servicio y marca la interfaz como no autenticada. */
    fun logout() {
        authenticationService.signOut()
        isAuthenticated = false
    }
    /** Expone el UID actual obtenido del servicio de autenticación. */
    fun getCurrentUserUid(): String {
        return authenticationService.getCurrentUserUid()
    }
}