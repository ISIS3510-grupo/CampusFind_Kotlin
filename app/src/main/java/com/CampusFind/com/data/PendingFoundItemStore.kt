package com.CampusFind.com.data

import android.content.Context
import android.net.Uri
import org.json.JSONObject
import java.io.File
import java.io.IOException
import android.util.Log

/** Persiste una cola por usuario en SharedPreferences y las fotos en archivos internos del teléfono. */
class PendingFoundItemStore(private val context: Context, userId: String) {

    private val preferences = context.getSharedPreferences(
        "pending_found_items_$userId",
        Context.MODE_PRIVATE
    )

    /** Copia la foto a almacenamiento interno y guarda el JSON bajo el ID fijo. Si reemplaza un pendiente, elimina su foto anterior después de persistir. */
    fun save(
        id: String,
        title: String,
        category: String,
        publicDescription: String,
        locationName: String,
        photoUri: Uri?,
        privateCharacteristics: String
    ) {
        // Aquí photoPath es una ruta LOCAL; el repositorio genera después la ruta de Storage.
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
            //if (photoPath.isNotEmpty()) File(photoPath).delete()
            if (photoPath.isNotEmpty() && !File(photoPath).delete()) {
                Log.w("PendingFoundItemStore", "Unable to delete temporary photo.")
            }
            throw IOException("Unable to save item offline.")
        }
        if (previous != null) {
            val previousPath = JSONObject(previous).getString("photoPath")
            //if (previousPath.isNotEmpty()) File(previousPath).delete()
            if (previousPath.isNotEmpty() && !File(previousPath).delete()) {
                Log.w("PendingFoundItemStore", "Unable to delete previous photo.")
            }
        }
    }

    /** Lee los JSON pendientes del usuario; el orden de SharedPreferences no garantiza orden cronológico. */
    fun list(): List<JSONObject> {
        return preferences.all.values.map { value ->
            JSONObject(value as String)
        }
    }

    /** Elimina el pendiente de SharedPreferences y luego su foto local. Lanza IOException si no logra confirmar la eliminación del registro. */
    fun delete(id: String) {
        val value = preferences.getString(id, null) ?: return
        val photoPath = JSONObject(value).getString("photoPath")
        if (!preferences.edit().remove(id).commit()) {
            throw IOException("Unable to remove pending item.")
        }
        //if (photoPath.isNotEmpty()) File(photoPath).delete()
        if (photoPath.isNotEmpty() && !File(photoPath).delete()) {
            Log.w("PendingFoundItemStore", "Unable to delete pending item photo.")
        }
    }
}
