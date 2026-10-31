package com.CampusFind.com.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.CampusFind.com.ui.components.CampusBorder
import com.CampusFind.com.ui.components.CampusGray
import com.CampusFind.com.ui.components.ScreenTitle
import com.CampusFind.com.viewmodel.AdminLostReportsViewModel
import java.text.DateFormat

@Composable
fun AdminLostReportsScreen(
    onBack: () -> Unit,
    viewModel: AdminLostReportsViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadReports()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ScreenTitle(
                title = "Search lost & found records",
                subtitle = "Review reported lost items",
                onBack = onBack
            )
        }
        if (viewModel.isLoading) {
            item { Text("Loading...", color = CampusGray) }
        }
        viewModel.errorMessage?.let { error ->
            item {
                Text(error, color = MaterialTheme.colorScheme.error)
                OutlinedButton(
                    onClick = viewModel::loadReports,
                    enabled = !viewModel.isLoading && viewModel.busyReportId == null
                ) {
                    Text("Reload reports")
                }
            }
        }
        viewModel.successMessage?.let { message ->
            item { Text(message, fontWeight = FontWeight.Medium) }
        }
        if (!viewModel.isLoading && viewModel.errorMessage == null && viewModel.reports.isEmpty()) {
            item { Text("No reported lost items", color = CampusGray) }
        }
        items(viewModel.reports, key = { it.id }) { report ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CampusBorder, RoundedCornerShape(10.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(report.title, fontWeight = FontWeight.Bold)
                Text("Category: ${report.category}")
                Text("Location: ${report.locationName}")
                Text(
                    "Reported at: ${report.reportedAt?.toDate()?.let {
                        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(it)
                    } ?: "Unavailable"}",
                    color = CampusGray
                )
                if (viewModel.busyReportId == report.id) {
                    Text("Processing...", color = CampusGray)
                }
                OutlinedButton(
                    onClick = { viewModel.markAsFound(report) },
                    enabled = !viewModel.isLoading && viewModel.busyReportId == null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Mark as found", color = Color.Black)
                }
            }
        }
    }
}
