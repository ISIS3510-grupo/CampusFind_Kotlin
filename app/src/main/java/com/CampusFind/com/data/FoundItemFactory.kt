package com.CampusFind.com.data

object FoundItemFactory {

    fun createPrivate(privateCharacteristics: String): Map<String, Any> {
        return mapOf("privateCharacteristics" to privateCharacteristics)
    }

    fun create(
        title: String,
        category: String,
        publicDescription: String,
        locationName: String,
        reporterUid: String,
        photoPath: String?
    ): Map<String, Any> {
        val item = mutableMapOf<String, Any>(
            "title" to title,
            "category" to category,
            "publicDescription" to publicDescription,
            "locationName" to locationName,
            "status" to "available",
            "reporterUid" to reporterUid,
            "semesterId" to "2026-2",
            "donationEligible" to false,
            "donationStatus" to "none"
        )

        if (!photoPath.isNullOrEmpty()) {
            item["photoPath"] = photoPath
        }

        return item
    }
}
