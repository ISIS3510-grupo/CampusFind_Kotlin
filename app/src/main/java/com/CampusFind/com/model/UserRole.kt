package com.CampusFind.com.model

enum class UserRole(val firestoreValue: String) {
    STUDENT("student"),
    ADMIN("admin");

    companion object {
        fun fromFirestore(value: String?): UserRole? =
            entries.firstOrNull { it.firestoreValue == value }
    }
}
