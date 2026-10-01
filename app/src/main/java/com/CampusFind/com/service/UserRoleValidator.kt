package com.CampusFind.com.service

import com.CampusFind.com.model.UserRole

object UserRoleValidator {

    fun isValid(value: String?, expectedRole: UserRole): Boolean {
        return UserRole.fromFirestore(value) == expectedRole
    }
}
