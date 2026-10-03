package com.CampusFind.com.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.CampusFind.com.data.ReportTimesRepository
import com.CampusFind.com.service.CategoryReportTime
import com.CampusFind.com.ui.components.CampusBorder
import com.CampusFind.com.ui.components.CampusGray
import com.CampusFind.com.ui.components.ScreenTitle
import com.CampusFind.com.ui.components.YellowBadge
import java.util.Locale

@Composable
fun AdminReportTimesScreen(onBack: () -> Unit) {
    val repository = remember { ReportTimesRepository() }
    var reportTimes by remember { mutableStateOf(emptyList<CategoryReportTime>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(repository) {
        repository.getReportTimes(
            onSuccess = { result ->
                reportTimes = result
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
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ScreenTitle(
            title = "Average time to found",
            subtitle = "Time from reported to found, by category",
            onBack = onBack
        )

        if (isLoading) {
            Text(text = "Loading...", color = CampusGray)
        } else if (errorMessage != null) {
            Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)
        } else if (reportTimes.isEmpty()) {
            Text(text = "No data yet", color = CampusGray)
        } else {
            for (reportTime in reportTimes) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = CampusBorder,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = reportTime.category, fontWeight = FontWeight.Bold)
                        Text(text = "${reportTime.count} reports", color = CampusGray)
                    }
                    YellowBadge(
                        text = String.format(Locale.US, "%.2f days", reportTime.averageDays)
                    )
                }
            }
        }
    }
}
