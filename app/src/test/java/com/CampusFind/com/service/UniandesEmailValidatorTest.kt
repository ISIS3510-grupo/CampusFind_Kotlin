package com.CampusFind.com.service

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UniandesEmailValidatorTest {

    @Test
    fun acceptsInstitutionalEmail() {
        assertTrue(UniandesEmailValidator.isValid("j.perez@uniandes.edu.co"))
    }

    @Test
    fun ignoresCaseAndSurroundingSpaces() {
        assertTrue(UniandesEmailValidator.isValid("  J.Perez@UNIANDES.EDU.CO "))
    }

    @Test
    fun rejectsOtherDomains() {
        assertFalse(UniandesEmailValidator.isValid("j.perez@gmail.com"))
        assertFalse(UniandesEmailValidator.isValid("j.perez@uniandes.edu.co.fake.com"))
    }

    @Test
    fun rejectsEmptyOrMalformedInput() {
        assertFalse(UniandesEmailValidator.isValid(null))
        assertFalse(UniandesEmailValidator.isValid("   "))
        assertFalse(UniandesEmailValidator.isValid("uniandes.edu.co"))
    }
}
