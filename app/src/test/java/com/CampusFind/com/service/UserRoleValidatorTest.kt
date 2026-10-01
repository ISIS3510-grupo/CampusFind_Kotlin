package com.CampusFind.com.service

import com.CampusFind.com.model.UserRole
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserRoleValidatorTest {

    @Test
    fun acceptsExpectedRole() {
        assertTrue(UserRoleValidator.isValid("student", UserRole.STUDENT))
        assertTrue(UserRoleValidator.isValid("admin", UserRole.ADMIN))
    }

    @Test
    fun rejectsOtherRole() {
        assertFalse(UserRoleValidator.isValid("admin", UserRole.STUDENT))
        assertFalse(UserRoleValidator.isValid("student", UserRole.ADMIN))
    }

    @Test
    fun rejectsMissingOrUnknownRoles() {
        for (role in UserRole.entries) {
            assertFalse(UserRoleValidator.isValid(null, role))
            assertFalse(UserRoleValidator.isValid("", role))
            assertFalse(UserRoleValidator.isValid("staff", role))
        }
    }

    @Test
    fun requiresExactRoleValue() {
        assertFalse(UserRoleValidator.isValid("Admin", UserRole.ADMIN))
        assertFalse(UserRoleValidator.isValid(" admin ", UserRole.ADMIN))
        assertFalse(UserRoleValidator.isValid("Student", UserRole.STUDENT))
        assertFalse(UserRoleValidator.isValid(" student ", UserRole.STUDENT))
    }
}
