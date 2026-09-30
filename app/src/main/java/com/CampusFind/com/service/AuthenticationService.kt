package com.CampusFind.com.service

import com.google.firebase.auth.FirebaseAuth

class AuthenticationService {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun signInStudent(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val normalizedEmail = email.trim().lowercase()


        if (!UniandesEmailValidator.isValid(normalizedEmail)) {
            onError("Use a valid @uniandes.edu.co email.")
            return
        }

        if (password.isBlank()) {
            onError("Password cannot be empty.")
            return
        }

        firebaseAuth
            .signInWithEmailAndPassword(
                normalizedEmail,
                password
            )
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {
                    onError(
                        task.exception?.localizedMessage
                            ?: "Unable to sign in."
                    )
                    return@addOnCompleteListener
                }

                val user = firebaseAuth.currentUser

                if (user == null) {
                    onError("Authentication failed.")
                    return@addOnCompleteListener
                }

                if (!UniandesEmailValidator.isValid(user.email)) {
                    firebaseAuth.signOut()
                    onError("This account is not a Uniandes account.")
                    return@addOnCompleteListener
                }

                if (!user.isEmailVerified) {
                    firebaseAuth.signOut()
                    onError("Please verify your Uniandes email before signing in.")
                    return@addOnCompleteListener
                }

                onSuccess()
            }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun isStudentAuthenticated(): Boolean {
        val user = firebaseAuth.currentUser

        return user != null &&
                user.isEmailVerified &&
                UniandesEmailValidator.isValid(user.email)
    }
}