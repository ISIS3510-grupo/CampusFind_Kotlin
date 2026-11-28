
package com.CampusFind.com.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.CampusFind.com.data.AdminFoundItemDetailRepository
import com.CampusFind.com.model.FoundItemDetail

class AdminFoundItemDetailViewModel : ViewModel() {

    private val repository = AdminFoundItemDetailRepository()

    var item by mutableStateOf<FoundItemDetail?>(null)
        private set

    var privateDetail by mutableStateOf<String?>(null)
        private set

    var photo by mutableStateOf<Bitmap?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var requestVersion = 0

    fun load(itemId: String) {
        val version = ++requestVersion

        isLoading = true
        item = null
        privateDetail = null
        photo = null
        errorMessage = null

        repository.load(
            itemId,
            onSuccess = { result, detail, photoPath ->
                if (version == requestVersion) {
                    item = result
                    privateDetail = detail
                    isLoading = false

                    if (!photoPath.isNullOrBlank()) {
                        repository.loadPhoto(photoPath) { bitmap ->
                            if (version == requestVersion) {
                                photo = bitmap
                            }
                        }
                    }
                }
            },
            onError = { error ->
                if (version == requestVersion) {
                    errorMessage = error
                    isLoading = false
                }
            }
        )
    }

    fun offline() {
        ++requestVersion
        isLoading = false
        privateDetail = null
        errorMessage = "Private details and actions are unavailable offline."
    }

    override fun onCleared() {
        ++requestVersion
        privateDetail = null
        photo = null
        super.onCleared()
    }
}
