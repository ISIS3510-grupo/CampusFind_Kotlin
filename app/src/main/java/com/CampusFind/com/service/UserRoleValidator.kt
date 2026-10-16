package com.CampusFind.com.service

import com.CampusFind.com.model.UserRole

/** Centraliza la comparación entre el rol almacenado y el rol requerido. */
object UserRoleValidator {

    /** Convierte el valor de Firestore a UserRole y exige igualdad con expectedRole; rechaza valores desconocidos o nulos. */
    fun isValid(value: String?, expectedRole: UserRole): Boolean {
        return UserRole.fromFirestore(value) == expectedRole
    }
}
