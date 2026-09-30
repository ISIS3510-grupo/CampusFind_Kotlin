package com.CampusFind.com.service

object UniandesEmailValidator {

    private const val UNIANDES_DOMAIN = "uniandes.edu.co"

    fun isValid(email: String?): Boolean {
        if (email.isNullOrBlank()) return false

        val normalizedEmail = email.trim().lowercase()
        val domain = normalizedEmail.substringAfterLast("@", "")

        return domain == UNIANDES_DOMAIN
    }
}