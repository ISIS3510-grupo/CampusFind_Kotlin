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
import com.CampusFind.com.data.OwnershipRatesRepository
import com.CampusFind.com.service.OwnershipRateCalculator
import java.util.Locale

@Composable
fun AdminOwnershipScreen(onBack: () -> Unit) {
    val repository = remember { OwnershipRatesRepository() }
    var rates by remember { mutableStateOf(OwnershipRateCalculator.calculate(emptyList())) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(repository) {
        repository.getOwnershipRates(
            onSuccess = { result ->
                rates = result
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
            text = "Verification rate",
            style = MaterialTheme.typography.headlineSmall
        )

        if (isLoading) {
            Text(text = "Loading...")
        } else if (errorMessage != null) {
            Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)
        } else if (rates.withPrivate.total == 0 && rates.withoutPrivate.total == 0) {
            Text(text = "No data yet")
        } else {
            Text(
                text = String.format(
                    Locale.US,
                    "With private characteristics: %d of %d (%.1f%%)",
                    rates.withPrivate.successful,
                    rates.withPrivate.total,
                    rates.withPrivate.ratePercent
                )
            )
            Text(
                text = String.format(
                    Locale.US,
                    "Without private characteristics: %d of %d (%.1f%%)",
                    rates.withoutPrivate.successful,
                    rates.withoutPrivate.total,
                    rates.withoutPrivate.ratePercent
                )
            )
            Text(
                text = String.format(
                    Locale.US,
                    "Difference: %.1f points",
                    rates.withPrivate.ratePercent - rates.withoutPrivate.ratePercent
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Button(onClick = onBack) {
            Text(text = "Back")
        }
    }
}
