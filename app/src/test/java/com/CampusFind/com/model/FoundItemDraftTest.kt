package com.CampusFind.com.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FoundItemDraftTest {

    @Test
    fun allBlankFieldsAreEmpty() {
        assertTrue(FoundItemDraft("", " ", "\t", "\n").isEmpty())
    }

    @Test
    fun anyNonBlankFieldMakesDraftNonEmpty() {
        assertFalse(FoundItemDraft("Electronics", "", "", "").isEmpty())
        assertFalse(FoundItemDraft("", "Charger", "", "").isEmpty())
        assertFalse(FoundItemDraft("", "", "Library", "").isEmpty())
        assertFalse(FoundItemDraft("", "", "", "9:10 AM").isEmpty())
    }
}
