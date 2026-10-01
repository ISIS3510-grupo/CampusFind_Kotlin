package com.CampusFind.com.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.CampusFind.com.data.ReportTimesRepository
import com.CampusFind.com.service.CategoryReportTime
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
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Average time to found",
            style = MaterialTheme.typography.headlineSmall
        )

        if (isLoading) {
            Text(text = "Loading...")
        } else if (errorMessage != null) {
            Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)
        } else if (reportTimes.isEmpty()) {
            Text(text = "No data yet")
        } else {
            for (reportTime in reportTimes) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = reportTime.category, modifier = Modifier.weight(1f))
                    Text(text = "${reportTime.count} reports")
                    Text(text = String.format(Locale.US, "%.2f days", reportTime.averageDays))
                }
            }
        }

        Button(onClick = onBack) {
            Text(text = "Back")
        }
    }
}
