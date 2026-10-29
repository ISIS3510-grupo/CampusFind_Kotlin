package com.CampusFind.com.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.CampusFind.com.data.LostReportRepository
import com.CampusFind.com.model.LostReport
import com.google.firebase.firestore.ListenerRegistration

class LostReportsViewModel : ViewModel() {

    private val repository = LostReportRepository()

    private var listener:
            ListenerRegistration? = null

    var reports by mutableStateOf<List<LostReport>>(emptyList())
        private set

    fun observeReports(
        ownerUid: String
    ) {
        listener?.remove()
        listener = null

        if (ownerUid.isBlank()) {
            reports = emptyList()
            return
        }

        listener = repository.observeReports(ownerUid) { reports = it }
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}
