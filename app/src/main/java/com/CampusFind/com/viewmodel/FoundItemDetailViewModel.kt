package com.CampusFind.com.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.CampusFind.com.data.FoundItemRepository
import com.CampusFind.com.model.FoundItemDetail

class FoundItemDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FoundItemRepository.getInstance(application)

    var foundItem by
    mutableStateOf<FoundItemDetail?>(null)
        private set

    fun loadFoundItem(
        foundItemId: String
    ) {
        if (foundItemId.isBlank()) {
            foundItem = null
            return
        }

        repository.getFoundItem(foundItemId) { foundItem = it }
    }
}
