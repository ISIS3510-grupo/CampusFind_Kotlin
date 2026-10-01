package com.CampusFind.com.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UserRoleTest {

    @Test
    fun parsesKnownRoles() {
        assertEquals(UserRole.STUDENT, UserRole.fromFirestore("student"))
        assertEquals(UserRole.ADMIN, UserRole.fromFirestore("admin"))
    }

    @Test
    fun rejectsUnknownOrMissingRoles() {
        assertNull(UserRole.fromFirestore(null))
        assertNull(UserRole.fromFirestore(""))
        assertNull(UserRole.fromFirestore("Admin"))
    }
}
