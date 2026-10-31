package com.CampusFind.com.data

import android.content.Context
import com.CampusFind.com.model.OfficeLocation

class OfficeLocationCache(
    context: Context
) {

    private val preferences =
        context.getSharedPreferences(
            "office_location_cache",
            Context.MODE_PRIVATE
        )

    fun saveOffice(office: OfficeLocation) {
        preferences.edit()
            .putString("id", office.id)
            .putString("name", office.name)
            .putString("code", office.code)
            .putString("address", office.address)

            .putLong("latitude", java.lang.Double.doubleToRawLongBits(office.latitude))
            .putLong("longitude", java.lang.Double.doubleToRawLongBits(office.longitude))
            .putLong("radiusMeters", java.lang.Double.doubleToRawLongBits(office.radiusMeters))

            .putBoolean("active", office.active)
            .putBoolean("has_cache", true)

            .apply()
    }

    fun getOffice(): OfficeLocation? {

        if (!preferences.getBoolean("has_cache", false)) {
            return null
        }

        return OfficeLocation(
            id = preferences.getString("id", "") ?: "",
            name = preferences.getString("name", "") ?: "",
            code = preferences.getString("code", "") ?: "",
            address = preferences.getString("address", "") ?: "",
            latitude = java.lang.Double.longBitsToDouble(preferences.getLong("latitude", 0L)),
            longitude = java.lang.Double.longBitsToDouble(preferences.getLong("longitude", 0L)),
            radiusMeters = java.lang.Double.longBitsToDouble(preferences.getLong("radiusMeters", 0L)),
            active = preferences.getBoolean("active",true)
        )
    }
}