package com.CampusFind.com.viewmodel

import android.os.SystemClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.CampusFind.com.data.MatchSearchTimeRepository
import com.CampusFind.com.data.SmartMatchingRepository
import com.CampusFind.com.model.LostReport
import com.CampusFind.com.model.MatchResult

class SmartMatchingViewModel : ViewModel() {

    private val repository =
        SmartMatchingRepository()

    private val matchSearchTimeRepository =
        MatchSearchTimeRepository()

    var matches by
    mutableStateOf<List<MatchResult>>(
        emptyList()
    )
        private set

    var isLoading by
    mutableStateOf(false)
        private set

    var errorMessage by
    mutableStateOf<String?>(null)
        private set

    fun generateMatches(
        lostReport: LostReport
    ) {

        if (
            lostReport.id.isBlank() ||
            lostReport.ownerUid.isBlank()
        ) {
            return
        }

        isLoading = true
        errorMessage = null

        val startTime =
            SystemClock.elapsedRealtime()

        repository.generateMatches(

            lostReport = lostReport,

            onSuccess = { results ->

                val durationMs =
                    SystemClock.elapsedRealtime() -
                            startTime

                matchSearchTimeRepository
                    .recordDuration(
                        durationMs
                    )

                matches = results
                isLoading = false
            },

            onError = { error ->

                errorMessage = error
                isLoading = false
            }
        )
    }
}