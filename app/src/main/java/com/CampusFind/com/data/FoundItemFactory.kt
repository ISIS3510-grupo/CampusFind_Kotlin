package com.CampusFind.com.data

/** Factory que separa los datos públicos del objeto de los detalles usados para verificar propiedad. */
object FoundItemFactory {

    /** Construye el documento privado con las características de propiedad; no lo mezcla con el mapa público. */
    fun createPrivate(privateCharacteristics: String): Map<String, Any> {
        return mapOf("privateCharacteristics" to privateCharacteristics)
    }

    /** Construye el mapa público con estado inicial y semestre fijo 2026-2. Incluye photoPath solo cuando hay foto; el repositorio agrega createdAt. */
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
