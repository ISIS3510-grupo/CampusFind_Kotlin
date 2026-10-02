package com.CampusFind.com.service

import com.CampusFind.com.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthenticationService {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private var validatedRole: UserRole? = null

    fun loginStudent(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        signIn(email, password, UserRole.STUDENT, onSuccess, onError)
    }

    fun loginAdmin(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        signIn(email, password, UserRole.ADMIN, onSuccess, onError)
    }

    private fun signIn(
        email: String,
        password: String,
        expectedRole: UserRole,
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
                    validatedRole = null

                    onError(
                        task.exception?.localizedMessage
                            ?: "Unable to sign in."
                    )

                    return@addOnCompleteListener
                }

                val user = firebaseAuth.currentUser

                if (user == null) {
                    validatedRole = null
                    onError("Authentication failed.")
                    return@addOnCompleteListener
                }

                if (!UniandesEmailValidator.isValid(user.email)) {
                    validatedRole = null
                    firebaseAuth.signOut()

                    onError(
                        "This account is not a Uniandes account."
                    )

                    return@addOnCompleteListener
                }

                if (!user.isEmailVerified) {
                    validatedRole = null
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
                            validatedRole = null
                            firebaseAuth.signOut()

                            onError(
                                "User profile not found."
                            )

                            return@addOnSuccessListener
                        }

                        val role =
                            document.getString("role")


                        if (!UserRoleValidator.isValid(role, expectedRole)) {
                            validatedRole = null
                            firebaseAuth.signOut()

                            onError(
                                "This account does not have ${expectedRole.firestoreValue} access."
                            )

                            return@addOnSuccessListener
                        }

                        validatedRole = expectedRole

                        onSuccess()
                    }
                    .addOnFailureListener { exception ->

                        validatedRole = null
                        firebaseAuth.signOut()

                        onError(
                            exception.localizedMessage
                                ?: "Unable to verify ${expectedRole.firestoreValue} role."
                        )
                    }
            }
    }

    fun getCurrentUserEmail(): String {
        return firebaseAuth.currentUser?.email ?: ""
    }

    fun signOut() {
        validatedRole = null
        firebaseAuth.signOut()
    }

    fun isStudentAuthenticated(): Boolean {

        val user = firebaseAuth.currentUser

        return user != null &&
                user.isEmailVerified &&
                UniandesEmailValidator.isValid(user.email) &&
                validatedRole == UserRole.STUDENT
    }

    fun getCurrentUserUid(): String {
        return firebaseAuth.currentUser?.uid ?: ""
    }
}
