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

    fun createId(): String {
        return firestore.collection("foundItems").document().id
    }

    fun saveOffline(
        id: String,
        title: String,
        category: String,
        publicDescription: String,
        locationName: String,
        photoUri: Uri?,
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
                id, title, category, publicDescription, locationName, photoUri
            )
        } catch (exception: Exception) {
            onError(exception.localizedMessage ?: "Unable to save item offline.")
            return
        }
        onSuccess()
        if (connectivityObserver.isConnected) sendPendingItems()
    }

    fun sendPendingItems() {
        if (isSendingPending || isSaving || !connectivityObserver.isConnected) return
        val user = firebaseAuth.currentUser ?: return
        val store = PendingFoundItemStore(context, user.uid)
        val items = store.list()
        isSendingPending = true
        sendNextPending(items, 0, store, user.uid)
    }

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

    fun saveFoundItem(
        id: String,
        title: String,
        category: String,
        publicDescription: String,
        locationName: String,
        photoUri: Uri?,
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
            saveDocument(id, item, onSuccess, onError)
            return
        }

        storage.reference.child(photoPath!!)
            .putFile(photoUri)
            .addOnSuccessListener {
                saveDocument(id, item, onSuccess, onError)
            }
            .addOnFailureListener { exception ->
                isSaving = false
                onError(exception.localizedMessage ?: "Unable to upload photo.")
            }
    }

    private fun saveDocument(
        id: String,
        item: Map<String, Any>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val document = item.toMutableMap()
        document["createdAt"] = FieldValue.serverTimestamp()
        firestore.collection("foundItems")
            .document(id)
            .set(document)
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

        fun getInstance(context: Context): FoundItemRepository {
            if (instance == null) {
                instance = FoundItemRepository(context.applicationContext)
            }
            return instance!!
        }
    }
}
