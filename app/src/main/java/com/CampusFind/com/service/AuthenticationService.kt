package com.CampusFind.com.service

import com.CampusFind.com.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/** Autentica con Firebase Auth y valida correo verificado y rol en users/{uid}. El rol validado se conserva solo en memoria. */
class AuthenticationService {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private var validatedRole: UserRole? = null

    /** Inicia sesión exigiendo el rol student y comunica el resultado mediante callbacks. */
    fun loginStudent(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        signIn(email, password, UserRole.STUDENT, onSuccess, onError)
    }

    /** Inicia sesión exigiendo el rol admin; el rol se lee del perfil de Firestore. */
    fun loginAdmin(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        signIn(email, password, UserRole.ADMIN, onSuccess, onError)
    }

    /** Valida dominio y contraseña, autentica, exige correo verificado y consulta el rol esperado. Cierra la sesión si la cuenta autenticada no supera la validación. */
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

    /** Devuelve el correo de la sesión Firebase actual, o una cadena vacía si no hay sesión. */
    fun getCurrentUserEmail(): String {
        return firebaseAuth.currentUser?.email ?: ""
    }

    /** Limpia el rol validado en memoria y cierra la sesión de Firebase Auth. */
    fun signOut() {
        validatedRole = null
        firebaseAuth.signOut()
    }

    /** Comprueba sesión, correo verificado, dominio Uniandes y rol STUDENT validado en esta instancia. */
    fun isStudentAuthenticated(): Boolean {

        val user = firebaseAuth.currentUser

        return user != null &&
                user.isEmailVerified &&
                UniandesEmailValidator.isValid(user.email) &&
                validatedRole == UserRole.STUDENT
    }

    /** Devuelve el UID de la sesión actual, o una cadena vacía si no existe usuario. */
    fun getCurrentUserUid(): String {
        return firebaseAuth.currentUser?.uid ?: ""
    }
}
