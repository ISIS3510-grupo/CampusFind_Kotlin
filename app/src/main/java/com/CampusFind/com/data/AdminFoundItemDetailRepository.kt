
package com.CampusFind.com.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.CampusFind.com.model.FoundItemDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage

class AdminFoundItemDetailRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun load(
        itemId: String,
        onSuccess: (FoundItemDetail, String?, String?) -> Unit,
        onError: (String) -> Unit
    ) {
        if (itemId.isBlank() || itemId.contains("/")) {
            onError("Invalid item ID.")
            return
        }

        val user = auth.currentUser

        if (user == null || !user.isEmailVerified ||
            !user.email.orEmpty().endsWith(
                "@uniandes.edu.co",
                ignoreCase = true
            )
        ) {
            onError("Verified administrator access is required.")
            return
        }

        firestore.collection("users")
            .document(user.uid)
            .get(Source.SERVER)
            .addOnSuccessListener { profile ->

                if (profile.getString("role") != "admin" ||
                    auth.currentUser?.uid != user.uid
                ) {
                    AdminFoundItemDetailCache.clear()
                    onError("Administrator access is required.")
                    return@addOnSuccessListener
                }

                val version = AdminFoundItemDetailCache.version()
                val cached = AdminFoundItemDetailCache.get(itemId)

                if (cached != null) {
                    loadPrivateDetail(
                        cached.item,
                        cached.photoPath,
                        user.uid,
                        version,
                        onSuccess,
                        onError
                    )
                } else {
                    firestore.collection("foundItems")
                        .document(itemId)
                        .get(Source.SERVER)
                        .addOnSuccessListener { document ->

                            if (auth.currentUser?.uid != user.uid ||
                                AdminFoundItemDetailCache.version() != version
                            ) {
                                onError("Item information changed. Please retry.")
                            } else if (!document.exists()) {
                                onError("Found item not found.")
                            } else {
                                val item = FoundItemDetail(
                                    id = document.id,
                                    title = document.getString("title") ?: "",
                                    locationName = document.getString("locationName") ?: "",
                                    createdAt = document.getTimestamp("createdAt"),
                                    status = document.getString("status") ?: "",
                                    category = document.getString("category") ?: ""
                                )

                                val photoPath = document.getString("photoPath")

                                AdminFoundItemDetailCache.put(
                                    item,
                                    photoPath,
                                    version
                                )

                                loadPrivateDetail(
                                    item,
                                    photoPath,
                                    user.uid,
                                    version,
                                    onSuccess,
                                    onError
                                )
                            }
                        }
                        .addOnFailureListener { e ->
                            onError(
                                e.localizedMessage
                                    ?: "Unable to load found item."
                            )
                        }
                }
            }
            .addOnFailureListener { e ->
                onError(
                    e.localizedMessage
                        ?: "Unable to verify administrator access."
                )
            }
    }

    private fun loadPrivateDetail(
        item: FoundItemDetail,
        photoPath: String?,
        uid: String,
        version: Long,
        onSuccess: (FoundItemDetail, String?, String?) -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("foundItemPrivate")
            .document(item.id)
            .get(Source.SERVER)
            .addOnSuccessListener { privateDoc ->
                if (auth.currentUser?.uid == uid &&
                    AdminFoundItemDetailCache.version() == version
                ) {
                    onSuccess(
                        item,
                        privateDoc.getString("privateCharacteristics"),
                        photoPath
                    )
                } else {
                    onError("Item information changed. Please retry.")
                }
            }
            .addOnFailureListener { e ->
                onError(
                    e.localizedMessage
                        ?: "Unable to load private ownership detail."
                )
            }
    }

    fun loadPhoto(
        path: String,
        onSuccess: (Bitmap?) -> Unit
    ) {
        if (path.isBlank()) {
            onSuccess(null)
            return
        }

        val storage = FirebaseStorage.getInstance()

        val reference =
            if (path.startsWith("gs://") ||
                path.startsWith("https://")
            ) {
                storage.getReferenceFromUrl(path)
            } else {
                storage.reference.child(path)
            }

        reference.getBytes(5L * 1024 * 1024)
            .addOnSuccessListener { bytes ->
                onSuccess(
                    BitmapFactory.decodeByteArray(
                        bytes,
                        0,
                        bytes.size
                    )
                )
            }
            .addOnFailureListener {
                onSuccess(null)
            }
    }
}
