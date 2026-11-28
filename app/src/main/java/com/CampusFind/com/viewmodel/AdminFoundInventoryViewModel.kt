
package com.CampusFind.com.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.CampusFind.com.data.AdminFoundInventoryRepository
import com.CampusFind.com.model.FoundItemDetail
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val SEARCH_DEBOUNCE_MS = 300L

class AdminFoundInventoryViewModel : ViewModel() {

    private val repository = AdminFoundInventoryRepository()

    var items by mutableStateOf<List<FoundItemDetail>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var allItems = emptyList<FoundItemDetail>()
    private var searchJob: Job? = null
    private var scheduledCount = 0
    private val executedCount = AtomicInteger()

    fun loadItems() {
        scheduledCount = 0
        executedCount.set(0)
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
        filterItems(debounce = true)
    }

    private fun filterItems(debounce: Boolean = false) {
        searchJob?.cancel()

        val query = searchQuery.trim()
        val currentItems = allItems
        scheduledCount++
        Log.d("InventorySearch", "scheduled=$scheduledCount query=$query")

        searchJob = viewModelScope.launch {
            if (debounce) {
                delay(SEARCH_DEBOUNCE_MS)
            }

            val filteredItems = withContext(Dispatchers.Default) {
                val execution = executedCount.incrementAndGet()

                Log.d(
                    "AdminInventory",
                    "Filtering on: ${Thread.currentThread().name}"
                )

                val startNanos = System.nanoTime()
                val results = currentItems.filter { item ->
                    query.isBlank() ||
                            item.title.contains(query, ignoreCase = true) ||
                            item.locationName.contains(query, ignoreCase = true)
                }
                val elapsedMs = (System.nanoTime() - startNanos) / 1_000_000.0
                val durationMs = String.format(Locale.US, "%.3f", elapsedMs)
                Log.d(
                    "InventorySearch",
                    "executed=$execution durationMs=$durationMs results=${results.size} " +
                            "items=${currentItems.size} thread=${Thread.currentThread().name}"
                )
                results
            }

            items = filteredItems
        }
    }

    override fun onCleared() {
        searchJob?.cancel()
        super.onCleared()
    }
}
