package com.CampusFind.com.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class FoundItemFactoryTest {

    @Test
    fun createsOnlyPrivateCharacteristics() {
        val item = FoundItemFactory.createPrivate("Serial ABC123 / unique mark")

        assertEquals(mapOf("privateCharacteristics" to "Serial ABC123 / unique mark"), item)
    }

    @Test
    fun publicItemDoesNotIncludePrivateCharacteristics() {
        val item = FoundItemFactory.create("Charger", "electronics", "Black charger", "Library", "user1", null)

        assertFalse(item.containsKey("privateCharacteristics"))
    }

    @Test
    fun preservesFieldsAndSetsDefaults() {
        val item = FoundItemFactory.create("Charger", "electronics", "Black charger", "Library", "user1", null)

        assertEquals("Charger", item["title"])
        assertEquals("electronics", item["category"])
        assertEquals("Black charger", item["publicDescription"])
        assertEquals("Library", item["locationName"])
        assertEquals("user1", item["reporterUid"])
        assertEquals("available", item["status"])
        assertEquals("2026-2", item["semesterId"])
        assertEquals(false, item["donationEligible"])
        assertEquals("none", item["donationStatus"])
    }

    @Test
    fun omitsNullOrEmptyPhotoPath() {
        val withoutPhoto = FoundItemFactory.create("Charger", "electronics", "Black charger", "Library", "user1", null)
        val emptyPhoto = FoundItemFactory.create("Charger", "electronics", "Black charger", "Library", "user1", "")

        assertFalse(withoutPhoto.containsKey("photoPath"))
        assertFalse(emptyPhoto.containsKey("photoPath"))
    }

    @Test
    fun includesPhotoPathWhenProvided() {
        val item = FoundItemFactory.create("Charger", "electronics", "Black charger", "Library", "user1", "foundItems/item1.jpg")

        assertEquals("foundItems/item1.jpg", item["photoPath"])
    }

    @Test
    fun doesNotIncludeCreatedAt() {
        val item = FoundItemFactory.create("Charger", "electronics", "Black charger", "Library", "user1", null)

        assertFalse(item.containsKey("createdAt"))
    }
}
