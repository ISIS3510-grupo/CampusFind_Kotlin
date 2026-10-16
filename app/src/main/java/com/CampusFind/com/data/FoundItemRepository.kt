package com.CampusFind.com.data

import android.content.Context
import android.net.Uri
import com.CampusFind.com.service.ConnectivityObserver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONObject
import java.io.File

/** Coordina Storage, Firestore y la cola local de objetos encontrados. Comparte una instancia con contexto de aplicación. */
class FoundItemRepository private constructor(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private var isSaving = false
    private var isSendingPending = false

    val connectivityObserver = ConnectivityObserver(context) { connected ->
        if (connected) sendPendingItems()
    }

    init {
        connectivityObserver.startListening()
    }

    /** Reserva un ID de Firestore sin escribir un documento. Se reutiliza al guardar y reintentar para evitar crear otros documentos. */
    fun createId(): String {
        return firestore.collection("foundItems").document().id
    }

    /** Guarda datos y copia de la foto en el teléfono para el usuario autenticado. onSuccess confirma persistencia local, no envío al servidor. */
    fun saveOffline(
        id: String,
        title: String,
        category: String,
        publicDescription: String,
        locationName: String,
        photoUri: Uri?,
        privateCharacteristics: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = firebaseAuth.currentUser
        if (user == null) {
            onError("Please sign in before registering an item.")
            return
        }
        try {
            PendingFoundItemStore(context, user.uid).save(
                id, title, category, publicDescription, locationName, photoUri, privateCharacteristics
            )
        } catch (exception: Exception) {
            onError(exception.localizedMessage ?: "Unable to save item offline.")
            return
        }
        onSuccess()
        if (connectivityObserver.isConnected) sendPendingItems()
    }

    /** Inicia el envío de pendientes del usuario actual si hay internet y no existe otro envío activo. */
    fun sendPendingItems() {
        if (isSendingPending || isSaving || !connectivityObserver.isConnected) return
        val user = firebaseAuth.currentUser ?: return
        val store = PendingFoundItemStore(context, user.uid)
        val items = store.list()
        isSendingPending = true
        sendNextPending(items, 0, store, user.uid)
    }

    /** Envía secuencialmente un pendiente con su ID original y lo elimina localmente solo tras el éxito. Se detiene ante errores, desconexión o cambio de usuario. */
    private fun sendNextPending(
        items: List<JSONObject>,
        index: Int,
        store: PendingFoundItemStore,
        userId: String
    ) {
        if (index >= items.size || !connectivityObserver.isConnected ||
            firebaseAuth.currentUser?.uid != userId) {
            isSendingPending = false
            return
        }
        val item = items[index]
        val photoPath = item.getString("photoPath")
        saveFoundItem(
            id = item.getString("id"),
            title = item.getString("title"),
            category = item.getString("category"),
            publicDescription = item.getString("publicDescription"),
            locationName = item.getString("locationName"),
            photoUri = if (photoPath.isEmpty()) null else Uri.fromFile(File(photoPath)),
            privateCharacteristics = item.optString("privateCharacteristics", ""),
            onSuccess = {
                try {
                    store.delete(item.getString("id"))
                } catch (exception: Exception) {
                    isSendingPending = false
                    return@saveFoundItem
                }
                sendNextPending(items, index + 1, store, userId)
            },
            onError = {
                isSendingPending = false
            }
        )
    }

    /** Prepara el mapa público y sube la foto opcional a foundItems/{id}.jpg antes de guardar en Firestore. Notifica éxito o error por callbacks. */
    fun saveFoundItem(
        id: String,
        title: String,
        category: String,
        publicDescription: String,
        locationName: String,
        photoUri: Uri?,
        privateCharacteristics: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = firebaseAuth.currentUser

        if (user == null) {
            onError("Please sign in before registering an item.")
            return
        }

        if (isSaving) {
            onError("Another item is being sent. Please try again.")
            return
        }
        isSaving = true
        // La ruta depende del ID original: un reintento usa el mismo archivo en Storage.
        val photoPath = if (photoUri == null) null else "foundItems/$id.jpg"
        val item = FoundItemFactory.create(
            title = title,
            category = category,
            publicDescription = publicDescription,
            locationName = locationName,
            reporterUid = user.uid,
            photoPath = photoPath
        )

        if (photoUri == null) {
            saveDocument(id, item, privateCharacteristics, onSuccess, onError)
            return
        }

        storage.reference.child(photoPath!!)
            .putFile(photoUri)
            .addOnSuccessListener {
                saveDocument(id, item, privateCharacteristics, onSuccess, onError)
            }
            .addOnFailureListener { exception ->
                isSaving = false
                onError(exception.localizedMessage ?: "Unable to upload photo.")
            }
    }

    /** Agrega la hora del servidor y escribe el documento público y el privado opcional en un lote atómico. Storage no forma parte de este lote. */
    private fun saveDocument(
        id: String,
        item: Map<String, Any>,
        privateCharacteristics: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val document = item.toMutableMap()
        // Evita depender del reloj del teléfono; un nuevo set también actualiza esta hora.
        document["createdAt"] = FieldValue.serverTimestamp()
        val batch = firestore.batch()
        // set escribe en el ID recibido, en lugar de generar otro con add.
        batch.set(firestore.collection("foundItems").document(id), document)
        if (privateCharacteristics.isNotBlank()) {
            batch.set(
                firestore.collection("foundItemPrivate").document(id),
                FoundItemFactory.createPrivate(privateCharacteristics)
            )
        }
        batch.commit()
            .addOnSuccessListener {
                val wasSendingPending = isSendingPending
                isSaving = false
                onSuccess()
                if (!wasSendingPending) sendPendingItems()
            }
            .addOnFailureListener { exception ->
                isSaving = false
                onError(exception.localizedMessage ?: "Unable to save found item.")
            }
    }

    companion object {
        private var instance: FoundItemRepository? = null

        /** Devuelve la instancia compartida y conserva applicationContext para no retener una Activity. */
        fun getInstance(context: Context): FoundItemRepository {
            if (instance == null) {
                instance = FoundItemRepository(context.applicationContext)
            }
            return instance!!
        }
    }
}
