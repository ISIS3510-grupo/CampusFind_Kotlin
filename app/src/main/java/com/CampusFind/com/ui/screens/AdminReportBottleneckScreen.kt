package com.CampusFind.com.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.CampusFind.com.ui.components.CampusBorder
import com.CampusFind.com.ui.components.CampusGray
import com.CampusFind.com.ui.components.ScreenTitle
import com.CampusFind.com.viewmodel.ReportBottleneckViewModel

@Composable
fun AdminReportBottleneckScreen(
    onBack: () -> Unit = {},
    bottleneckViewModel:
    ReportBottleneckViewModel =
        viewModel()
) {

    LaunchedEffect(Unit) {
        bottleneckViewModel.load()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(24.dp)
    ) {

        ScreenTitle(
            title = "Report bottleneck",
            subtitle =
                "Reports that have remained in the same stage for more than 7 days",
            onBack = onBack
        )

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        if (bottleneckViewModel.isLoading) {

            CircularProgressIndicator()

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )
        }

        val summary =
            bottleneckViewModel.summary

        if (summary != null) {

            BottleneckRow(
                label = "Reported",
                count =
                    summary.counts["reported"]
                        ?: 0
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            BottleneckRow(
                label = "Found",
                count =
                    summary.counts["found"]
                        ?: 0
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            BottleneckRow(
                label = "Ready for pickup",
                count =
                    summary.counts[
                        "ready_for_pickup"
                    ] ?: 0
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            BottleneckRow(
                label = "Claimed",
                count =
                    summary.counts["claimed"]
                        ?: 0
            )

            Spacer(
                modifier =
                    Modifier.height(28.dp)
            )

            Text(
                text = "Most common bottleneck",
                fontSize = 14.sp,
                color = CampusGray
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            val bottleneckText =
                if (
                    summary.mostStuckStages
                        .isEmpty()
                ) {

                    "No stuck reports"

                } else {

                    summary
                        .mostStuckStages
                        .joinToString(", ") {
                            formatStage(it)
                        }
                }

            Text(
                text = bottleneckText,
                fontSize = 22.sp,
                fontWeight =
                    FontWeight.Bold
            )

            if (
                summary
                    .missingTimestampReportIds
                    .isNotEmpty()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )

                Text(
                    text =
                        "${summary.missingTimestampReportIds.size} reports could not be evaluated because statusChangedAt is missing.",
                    fontSize = 13.sp,
                    color = CampusGray
                )
            }
        }

        if (
            bottleneckViewModel
                .errorMessage != null
        ) {

            Text(
                text =
                    bottleneckViewModel
                        .errorMessage
                        ?: ""
            )
        }

        Spacer(
            modifier =
                Modifier.height(28.dp)
        )

        Button(
            enabled =
                !bottleneckViewModel
                    .isLoading,

            onClick = {
                bottleneckViewModel.load()
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(
                text = "Refresh analytics"
            )
        }
    }
}

@Composable
private fun BottleneckRow(
    label: String,
    count: Int
) {

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
            .padding(18.dp),

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(
            text = label,
            fontSize = 16.sp
        )

        Text(
            text = count.toString(),
            fontSize = 18.sp,
            fontWeight =
                FontWeight.Bold
        )
    }
}

private fun formatStage(
    stage: String
): String {

    return when (stage) {

        "reported" ->
            "Reported"

        "found" ->
            "Found"

        "ready_for_pickup" ->
            "Ready for pickup"

        "claimed" ->
            "Claimed"

        else ->
            stage
    }
}