package com.CampusFind.com.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.CampusFind.com.data.FoundItemDraftStore
import com.CampusFind.com.data.FoundItemRepository
import com.CampusFind.com.model.FoundItemDraft
import com.google.firebase.auth.FirebaseAuth
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminRegisterFoundViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FoundItemRepository.getInstance(application)
    private val auth = FirebaseAuth.getInstance()
    private val uid = auth.currentUser?.uid
    private val store = uid?.let { FoundItemDraftStore(application, it) }

    var category by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set
    var location by mutableStateOf("")
        private set
    var time by mutableStateOf("")
        private set
    var privateCharacteristics by mutableStateOf("")
        private set
    var isSaving by mutableStateOf(false)
        private set
    var draftRestored by mutableStateOf(false)
        private set
    var isDraftSaved by mutableStateOf(false)
        private set

    val isConnected: Boolean
        get() = repository.connectivityObserver.isConnected

    private var autosaveJob: Job? = null
    private var draftChanged = false
    private var draftDirty = false
    private var itemId: String? = null
    private var registeredOffline: Boolean? = null
    private val canEdit: Boolean
        get() = !isSaving && registeredOffline == null

    init {
        viewModelScope.launch {
            val draft = try {
                withContext(Dispatchers.IO) { store?.read() }
            } catch (_: IOException) {
                null
            }
            if (draft != null && !draftChanged && !isSaving && registeredOffline == null) {
                category = draft.category
                description = draft.description
                location = draft.location
                time = draft.time
                draftRestored = true
                isDraftSaved = true
            }
        }
        repository.sendPendingItems()
    }

    fun onCategoryChange(value: String) {
        if (!canEdit) return
        category = value
        scheduleAutosave()
    }

    fun onDescriptionChange(value: String) {
        if (!canEdit) return
        description = value
        scheduleAutosave()
    }

    fun onLocationChange(value: String) {
        if (!canEdit) return
        location = value
        scheduleAutosave()
    }

    fun onTimeChange(value: String) {
        if (!canEdit) return
        time = value
        scheduleAutosave()
    }

    fun onPrivateCharacteristicsChange(value: String) {
        if (canEdit) privateCharacteristics = value
    }

    private fun scheduleAutosave() {
        draftChanged = true
        draftDirty = true
        autosaveJob?.cancel()
        val draft = currentDraft()
        autosaveJob = viewModelScope.launch {
            delay(400)
            persistDraft(draft)
        }
    }

    fun flushDraft(onComplete: () -> Unit = {}) {
        if (isSaving) return
        if (!draftDirty || registeredOffline != null) {
            onComplete()
            return
        }
        autosaveJob?.cancel()
        val draft = currentDraft()
        autosaveJob = viewModelScope.launch {
            persistDraft(draft)
            onComplete()
        }
    }

    private fun currentDraft() = FoundItemDraft(category, description, location, time)

    private suspend fun persistDraft(draft: FoundItemDraft) {
        if (store == null) return
        try {
            withContext(Dispatchers.IO) {
                store.save(draft.category, draft.description, draft.location, draft.time)
            }
            draftDirty = false
            isDraftSaved = !draft.isEmpty()
        } catch (_: IOException) {
            draftDirty = true
        }
    }

    fun register(
        photoUri: Uri?,
        onSaved: () -> Unit,
        onOfflineSaved: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (isSaving) return
        if (category.isBlank() || description.isBlank() || location.isBlank()) {
            onError("Fill category, description and location.")
            return
        }
        if (uid == null || auth.currentUser?.uid != uid) {
            onError("Please sign in before registering an item.")
            return
        }

        isSaving = true
        viewModelScope.launch {
            autosaveJob?.cancelAndJoin()
            if (registeredOffline == null) {
                persistDraft(currentDraft())
                submit(photoUri, onSaved, onOfflineSaved, onError)
            } else {
                finish(onSaved, onOfflineSaved, onError)
            }
        }
    }

    private fun submit(
        photoUri: Uri?,
        onSaved: () -> Unit,
        onOfflineSaved: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (auth.currentUser?.uid != uid) {
            isSaving = false
            onError("Please sign in before registering an item.")
            return
        }
        val id = itemId ?: repository.createId().also { itemId = it }
        val offline = !isConnected
        val onSuccess = {
            registeredOffline = offline
            finish(onSaved, onOfflineSaved, onError)
        }
        val onFailure: (String) -> Unit = { error ->
            isSaving = false
            onError(error)
        }

        if (offline) {
            repository.saveOffline(
                id, description, category, description, location, photoUri, privateCharacteristics,
                onSuccess, onFailure
            )
        } else {
            repository.saveFoundItem(
                id, description, category, description, location, photoUri, privateCharacteristics,
                onSuccess, onFailure
            )
        }
    }

    private fun finish(
        onSaved: () -> Unit,
        onOfflineSaved: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { store?.clear() }
            } catch (_: IOException) {
                isSaving = false
                onError("Item registered, but the draft could not be cleared. Try again.")
                return@launch
            }
            draftDirty = false
            isDraftSaved = false
            draftRestored = false
            isSaving = false
            if (registeredOffline == true) onOfflineSaved() else onSaved()
        }
    }
}
