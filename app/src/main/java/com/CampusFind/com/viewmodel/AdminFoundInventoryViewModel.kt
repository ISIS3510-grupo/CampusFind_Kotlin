
package com.CampusFind.com.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.CampusFind.com.data.AdminFoundInventoryRepository
import com.CampusFind.com.model.FoundItemDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminFoundInventoryViewModel : ViewModel() {

    private val repository = AdminFoundInventoryRepository()

    private var allItems = emptyList<FoundItemDetail>()

    var items by mutableStateOf<List<FoundItemDetail>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var searchJob: Job? = null

    fun loadItems() {
        isLoading = true
        errorMessage = null

        repository.loadItems(
            onSuccess = { result ->
                allItems = result
                isLoading = false
                filterItems()
            },
            onError = { error ->
                isLoading = false
                errorMessage = error
            }
        )
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        filterItems()
    }

    private fun filterItems() {
        searchJob?.cancel()

        val query = searchQuery.trim()
        val currentItems = allItems

        searchJob = viewModelScope.launch {

            val filteredItems = withContext(Dispatchers.Default) {

                Log.d(
                    "AdminInventory",
                    "Filtering on: ${Thread.currentThread().name}"
                )

                currentItems.filter { item ->
                    query.isBlank() ||
                            item.title.contains(query, ignoreCase = true) ||
                            item.locationName.contains(query, ignoreCase = true)
                }
            }

            items = filteredItems
        }
    }

    override fun onCleared() {
        searchJob?.cancel()
        super.onCleared()
    }
}
