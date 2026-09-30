package com.CampusFind.com.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthenticationService {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private var studentRoleValidated = false

    fun signInStudent(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val normalizedEmail = email.trim().lowercase()

        /*
         * Reuse the Uniandes email validation
         * implemented by Alex.
         */
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
                    studentRoleValidated = false

                    onError(
                        task.exception?.localizedMessage
                            ?: "Unable to sign in."
                    )

                    return@addOnCompleteListener
                }

                val user = firebaseAuth.currentUser

                if (user == null) {
                    studentRoleValidated = false
                    onError("Authentication failed.")
                    return@addOnCompleteListener
                }

                if (!UniandesEmailValidator.isValid(user.email)) {
                    studentRoleValidated = false
                    firebaseAuth.signOut()

                    onError(
                        "This account is not a Uniandes account."
                    )

                    return@addOnCompleteListener
                }

                if (!user.isEmailVerified) {
                    studentRoleValidated = false
                    firebaseAuth.signOut()

                    onError(
                        "Please verify your Uniandes email before signing in."
                    )

                    return@addOnCompleteListener
                }

                firestore
                    .collection("users")
                    .document(user.uid)
                    .get()
                    .addOnSuccessListener { document ->

                        if (!document.exists()) {
                            studentRoleValidated = false
                            firebaseAuth.signOut()

                            onError(
                                "User profile not found."
                            )

                            return@addOnSuccessListener
                        }

                        val role =
                            document.getString("role")

                        /*
                         * Student Authentication must only
                         * allow accounts whose Firestore
                         * role is exactly "student".
                         */
                        if (role != "student") {
                            studentRoleValidated = false
                            firebaseAuth.signOut()

                            onError(
                                "This account does not have student access."
                            )

                            return@addOnSuccessListener
                        }

                        studentRoleValidated = true

                        onSuccess()
                    }
                    .addOnFailureListener { exception ->

                        studentRoleValidated = false
                        firebaseAuth.signOut()

                        onError(
                            exception.localizedMessage
                                ?: "Unable to verify student role."
                        )
                    }
            }
    }

    fun signOut() {
        studentRoleValidated = false
        firebaseAuth.signOut()
    }

    fun isStudentAuthenticated(): Boolean {

        val user = firebaseAuth.currentUser

        return user != null &&
                user.isEmailVerified &&
                UniandesEmailValidator.isValid(user.email) &&
                studentRoleValidated
    }
}