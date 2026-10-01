package com.CampusFind.com.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {

    private val firestore =
        FirebaseFirestore.getInstance()

    var displayName by mutableStateOf("")
        private set

    var role by mutableStateOf("")
        private set

    fun loadUser(
        uid: String
    ) {
        if (uid.isBlank()) {
            return
        }

        firestore
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                displayName =
                    document.getString("displayName")
                        ?: ""

                role =
                    document.getString("role")
                        ?: ""
            }
    }
}