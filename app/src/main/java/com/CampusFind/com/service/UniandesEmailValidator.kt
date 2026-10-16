package com.CampusFind.com.service

/** Comprueba el dominio institucional usado por el flujo de autenticación. */
object UniandesEmailValidator {

    private const val UNIANDES_DOMAIN = "uniandes.edu.co"

    /** Rechaza valores vacíos y comprueba que el dominio tras el último @ sea uniandes.edu.co. No valida por sí solo toda la sintaxis del correo. */
    fun isValid(email: String?): Boolean {
        if (email.isNullOrBlank()) return false

        val normalizedEmail = email.trim().lowercase()
        val domain = normalizedEmail.substringAfterLast("@", "")

        return domain == UNIANDES_DOMAIN
    }
}