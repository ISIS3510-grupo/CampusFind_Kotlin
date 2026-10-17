package com.CampusFind.com.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.CampusFind.com.data.FoundItemRepository

class FoundItemFormViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FoundItemRepository.getInstance(application)

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

    val isConnected: Boolean
        get() = repository.connectivityObserver.isConnected

    private var itemId: String? = null

    private var locationInitialized = false

    fun onCategoryChange(value: String) {
        category = value
    }

    fun onDescriptionChange(value: String) {
        description = value
    }

    fun onLocationChange(value: String) {
        location = value
    }

    fun onTimeChange(value: String) {
        time = value
    }

    fun onPrivateCharacteristicsChange(value: String) {
        privateCharacteristics = value
    }

    fun initLocation(suggested: String) {
        if (locationInitialized) {
            return
        }
        locationInitialized = true
        location = suggested
    }

    fun sendPending() {
        repository.sendPendingItems()
    }

    fun save(
        photoUri: Uri?,
        onSaved: () -> Unit,
        onOfflineSaved: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (isSaving) {
            return
        }
        if (itemId == null) {
            itemId = repository.createId()
        }
        val id = itemId!!
        isSaving = true

        if (!isConnected) {
            repository.saveOffline(
                id = id,
                title = description,
                category = category,
                publicDescription = description,
                locationName = location,
                photoUri = photoUri,
                privateCharacteristics = privateCharacteristics,
                onSuccess = {
                    isSaving = false
                    onOfflineSaved()
                },
                onError = { error ->
                    isSaving = false
                    onError(error)
                }
            )
        } else {
            repository.saveFoundItem(
                id = id,
                title = description,
                category = category,
                publicDescription = description,
                locationName = location,
                photoUri = photoUri,
                privateCharacteristics = privateCharacteristics,
                onSuccess = {
                    isSaving = false
                    onSaved()
                },
                onError = { error ->
                    isSaving = false
                    onError(error)
                }
            )
        }
    }
}
