package com.CampusFind.com.data

import android.content.Context
import android.net.Uri
import org.json.JSONObject
import java.io.File
import java.io.IOException

class PendingFoundItemStore(private val context: Context, userId: String) {

    private val preferences = context.getSharedPreferences(
        "pending_found_items_$userId",
        Context.MODE_PRIVATE
    )

    fun save(
        id: String,
        title: String,
        category: String,
        publicDescription: String,
        locationName: String,
        photoUri: Uri?,
        privateCharacteristics: String
    ) {
        var photoPath = ""
        if (photoUri != null) {
            val directory = File(context.filesDir, "pending_found_items")
            directory.mkdirs()
            val photo = File.createTempFile("${id}_", ".jpg", directory)
            val input = context.contentResolver.openInputStream(photoUri)
                ?: throw IOException("Unable to read photo.")
            input.use { source ->
                photo.outputStream().use { destination ->
                    source.copyTo(destination)
                }
            }
            photoPath = photo.absolutePath
        }

        val previous = preferences.getString(id, null)
        val item = JSONObject()
            .put("id", id)
            .put("title", title)
            .put("category", category)
            .put("publicDescription", publicDescription)
            .put("locationName", locationName)
            .put("photoPath", photoPath)
            .put("privateCharacteristics", privateCharacteristics)

        if (!preferences.edit().putString(id, item.toString()).commit()) {
            if (photoPath.isNotEmpty()) File(photoPath).delete()
            throw IOException("Unable to save item offline.")
        }
        if (previous != null) {
            val previousPath = JSONObject(previous).getString("photoPath")
            if (previousPath.isNotEmpty()) File(previousPath).delete()
        }
    }

    fun list(): List<JSONObject> {
        return preferences.all.values.map { value ->
            JSONObject(value as String)
        }
    }

    fun delete(id: String) {
        val value = preferences.getString(id, null) ?: return
        val photoPath = JSONObject(value).getString("photoPath")
        if (!preferences.edit().remove(id).commit()) {
            throw IOException("Unable to remove pending item.")
        }
        if (photoPath.isNotEmpty()) File(photoPath).delete()
    }
}
