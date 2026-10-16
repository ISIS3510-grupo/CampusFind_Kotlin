package com.CampusFind.com.model

/** Roles reconocidos por la aplicación y sus valores exactos en Firestore. */
enum class UserRole(val firestoreValue: String) {
    STUDENT("student"),
    ADMIN("admin");

    companion object {
        /** Busca un rol con el valor exacto almacenado; devuelve null para un valor nulo o desconocido. */
        fun fromFirestore(value: String?): UserRole? =
            entries.firstOrNull { it.firestoreValue == value }
    }
}
