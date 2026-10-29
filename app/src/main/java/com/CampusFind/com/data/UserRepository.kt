package com.CampusFind.com.data

import com.CampusFind.com.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun getUser(uid: String, onSuccess: (UserProfile) -> Unit) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                onSuccess(UserProfile(
                    displayName = document.getString("displayName") ?: "",
                    role = document.getString("role") ?: ""
                ))
            }
    }
}
