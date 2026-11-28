
package com.CampusFind.com.data

import android.content.Context
import com.CampusFind.com.model.SearchFoundItem
import org.json.JSONArray
import org.json.JSONObject

class FoundItemsLocalStore(
    context: Context,
    userId: String
) {

    private val preferences = context.getSharedPreferences(
        "search_found_items_$userId",
        Context.MODE_PRIVATE
    )

    fun saveItems(items: List<SearchFoundItem>) {
        val jsonArray = JSONArray()

        items.forEach { item ->
            val json = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("category", item.category)
                put("locationName", item.locationName)
                put("publicDescription", item.publicDescription)
                put("photoPath", item.photoPath ?: JSONObject.NULL)
                put("createdAtMillis", item.createdAtMillis ?: JSONObject.NULL)
            }

            jsonArray.put(json)
        }

        val saved = preferences.edit()
            .putString("items", jsonArray.toString())
            .putLong("lastUpdated", System.currentTimeMillis())
            .commit()

        if (!saved) {
            throw IllegalStateException("Unable to save found items locally.")
        }
    }

    fun loadItems(): List<SearchFoundItem> {
        val savedData = preferences.getString("items", null)
            ?: return emptyList()

        return runCatching {
            val jsonArray = JSONArray(savedData)

            (0 until jsonArray.length()).map { index ->
                val json = jsonArray.getJSONObject(index)

                SearchFoundItem(
                    id = json.optString("id"),
                    title = json.optString("title"),
                    category = json.optString("category"),
                    locationName = json.optString("locationName"),
                    publicDescription = json.optString("publicDescription"),
                    photoPath = if (json.isNull("photoPath")) {
                        null
                    } else {
                        json.optString("photoPath")
                    },
                    createdAtMillis = if (json.isNull("createdAtMillis")) {
                        null
                    } else {
                        json.optLong("createdAtMillis")
                    }
                )
            }
        }.getOrDefault(emptyList())
    }

    fun getLastUpdated(): Long {
        return preferences.getLong("lastUpdated", 0L)
    }
}
