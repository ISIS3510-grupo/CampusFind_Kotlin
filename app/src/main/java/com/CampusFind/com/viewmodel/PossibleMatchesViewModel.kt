package com.CampusFind.com.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.CampusFind.com.data.PossibleMatchesRepository
import com.CampusFind.com.model.PossibleMatch
import com.google.firebase.firestore.ListenerRegistration

class PossibleMatchesViewModel : ViewModel() {

    private val repository =
        PossibleMatchesRepository()

    var matches by
    mutableStateOf<List<PossibleMatch>>(
        emptyList()
    )
        private set

    var isLoading by
    mutableStateOf(false)
        private set

    var errorMessage by
    mutableStateOf<String?>(null)
        private set

    val matchCount: Int
        get() = matches.size

    private var listener:
            ListenerRegistration? = null

    var selectedFoundItemId by mutableStateOf<String?>(null)
        private set

    var selectedMatchError by mutableStateOf<String?>(null)
        private set

    fun loadMatch(matchId: String) {
        selectedFoundItemId = null
        selectedMatchError = null

        repository.getFoundItemIdForMatch(
            matchId = matchId,

            onSuccess = { foundItemId ->
                selectedFoundItemId = foundItemId
            },

            onError = { error ->
                selectedMatchError = error
            }
        )
    }

    fun observeMatches(
        reportId: String
    ) {

        listener?.remove()

        isLoading = true
        errorMessage = null

        listener =
            repository.observePossibleMatches(

                reportId = reportId,

                onUpdate = { result ->

                    matches = result
                    isLoading = false
                },

                onError = { error ->

                    errorMessage = error
                    isLoading = false
                }
            )
    }

    override fun onCleared() {

        listener?.remove()

        super.onCleared()
    }
}
