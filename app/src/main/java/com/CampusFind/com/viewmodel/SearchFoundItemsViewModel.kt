
package com.CampusFind.com.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.CampusFind.com.data.SearchFoundItemsRepository
import com.CampusFind.com.model.SearchFoundItem
import kotlinx.coroutines.launch

class SearchFoundItemsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        SearchFoundItemsRepository(application)

    private var allItems = emptyList<SearchFoundItem>()

    var availableCategories by mutableStateOf(listOf("All"))
        private set

    private var loadGeneration = 0

    var items by mutableStateOf<List<SearchFoundItem>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
        private set

    var selectedCategory by mutableStateOf("All")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isShowingCachedData by mutableStateOf(false)
        private set

    var hasSavedData by mutableStateOf(false)
        private set

    var lastUpdatedMillis by mutableStateOf(0L)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadItems(isConnected: Boolean) {
        val userId = repository.getCurrentUserId()
        val generation = ++loadGeneration

        allItems = emptyList()
        applyFilters()
        hasSavedData = false
        isShowingCachedData = false
        lastUpdatedMillis = 0L
        errorMessage = null
        isLoading = true

        if (userId == null) {
            isLoading = false
            errorMessage = "Please sign in to search found items."
            return
        }

        viewModelScope.launch {

            // 1. Load local data first.
            try {
                val (cachedItems, updatedAt) =
                    repository.loadCachedItems(userId)

                if (generation != loadGeneration) {
                    return@launch
                }

                allItems = cachedItems
                lastUpdatedMillis = updatedAt
                hasSavedData = updatedAt > 0L
                isShowingCachedData = hasSavedData

                applyFilters()

            } catch (exception: Exception) {
                if (generation != loadGeneration) {
                    return@launch
                }

                errorMessage = "Unable to read locally saved items."
            }

            if (generation != loadGeneration) {
                return@launch
            }

            // 2. Offline: keep local results available.
            if (!isConnected) {
                isLoading = false
                return@launch
            }

            // 3. Online: fetch the latest Firebase data.
            repository.fetchAvailableItems(
                onSuccess = { freshItems ->

                    if (
                        generation != loadGeneration ||
                        repository.getCurrentUserId() != userId
                    ) {
                        return@fetchAvailableItems
                    }

                    allItems = freshItems
                    isShowingCachedData = false
                    isLoading = false
                    errorMessage = null

                    applyFilters()

                    // 4. Update local storage.
                    viewModelScope.launch {
                        try {
                            repository.saveCachedItems(
                                userId,
                                freshItems
                            )

                            if (generation == loadGeneration) {
                                hasSavedData = true
                                lastUpdatedMillis =
                                    System.currentTimeMillis()
                            }

                        } catch (exception: Exception) {
                            if (generation == loadGeneration) {
                                errorMessage =
                                    "Items loaded, but local storage could not be updated."
                            }
                        }
                    }
                },

                onError = { error ->
                    if (generation != loadGeneration) {
                        return@fetchAvailableItems
                    }

                    isLoading = false
                    errorMessage = error

                    // Previously loaded local data remains visible.
                }
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        applyFilters()
    }

    fun onCategorySelected(category: String) {
        selectedCategory = category
        applyFilters()
    }

    private fun applyFilters() {
        val query = searchQuery.trim()
        availableCategories = listOf("All") +
                allItems
                    .map { it.category.trim() }
                    .filter { it.isNotBlank() }
                    .distinctBy { it.lowercase() }
                    .sortedBy { it.lowercase() }

        items = allItems.filter { item ->

            val matchesSearch =
                query.isBlank() ||
                        item.title.contains(query, ignoreCase = true) ||
                        item.locationName.contains(query, ignoreCase = true) ||
                        item.publicDescription.contains(
                            query,
                            ignoreCase = true
                        )

            val matchesCategory =
                selectedCategory == "All" ||
                        item.category.equals(
                            selectedCategory,
                            ignoreCase = true
                        )

            matchesSearch && matchesCategory
        }
    }
}
