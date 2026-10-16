package com.CampusFind.com.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.CampusFind.com.data.MatchSearchTimeRepository
import com.CampusFind.com.data.MatchSearchTimeSummary
import com.CampusFind.com.ui.components.CampusBorder
import com.CampusFind.com.ui.components.CampusGray
import com.CampusFind.com.ui.components.ScreenTitle
import com.CampusFind.com.ui.components.YellowBadge
import androidx.compose.foundation.layout.statusBarsPadding
import java.util.Locale

@Composable
fun AdminMatchSearchTimeScreen(
    onBack: () -> Unit
) {

    val repository =
        remember {
            MatchSearchTimeRepository()
        }

    var summary by remember {
        mutableStateOf(
            MatchSearchTimeSummary()
        )
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(repository) {

        repository.getSummary(

            onSuccess = { result ->

                summary = result
                isLoading = false
            },

            onError = { error ->

                errorMessage = error
                isLoading = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 24.dp,
                vertical = 22.dp
            ),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

        ScreenTitle(
            title =
                "Match search performance",

            subtitle =
                "Average response time when searching for possible matches",

            onBack = onBack
        )

        when {

            isLoading -> {

                Text(
                    text = "Loading...",
                    color = CampusGray
                )
            }

            errorMessage != null -> {

                Text(
                    text =
                        errorMessage ?: "",

                    color =
                        MaterialTheme
                            .colorScheme
                            .error
                )
            }

            summary.sampleCount == 0 -> {

                Text(
                    text =
                        "No match search measurements yet.",
                    color = CampusGray
                )
            }

            else -> {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = CampusBorder,
                            shape =
                                RoundedCornerShape(
                                    10.dp
                                )
                        )
                        .padding(14.dp),

                    horizontalArrangement =
                        Arrangement.spacedBy(
                            12.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column(
                        modifier =
                            Modifier.weight(1f),

                        verticalArrangement =
                            Arrangement.spacedBy(
                                4.dp
                            )
                    ) {

                        Text(
                            text =
                                "Average match-search time",
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text =
                                "${summary.sampleCount} searches",
                            color =
                                CampusGray
                        )
                    }

                    YellowBadge(
                        text =
                            String.format(
                                Locale.US,
                                "%.2f s",
                                (summary.averageMs
                                    ?: 0.0) /
                                        1000.0
                            )
                    )
                }
            }
        }
    }
}