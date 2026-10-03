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
import com.CampusFind.com.data.OwnershipRatesRepository
import com.CampusFind.com.service.OwnershipRateCalculator
import com.CampusFind.com.ui.components.CampusBorder
import com.CampusFind.com.ui.components.CampusGray
import com.CampusFind.com.ui.components.ScreenTitle
import com.CampusFind.com.ui.components.YellowBadge
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
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ScreenTitle(
            title = "Verification rate",
            subtitle = "Successful ownership verifications, with and without private details",
            onBack = onBack
        )

        if (isLoading) {
            Text(text = "Loading...", color = CampusGray)
        } else if (errorMessage != null) {
            Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)
        } else if (rates.withPrivate.total == 0 && rates.withoutPrivate.total == 0) {
            Text(text = "No data yet", color = CampusGray)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = CampusBorder,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "With private characteristics",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    YellowBadge(
                        text = String.format(Locale.US, "%.1f%%", rates.withPrivate.ratePercent)
                    )
                }
                Text(
                    text = "${rates.withPrivate.successful} of ${rates.withPrivate.total} verified",
                    color = CampusGray
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = CampusBorder,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Without private characteristics",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    YellowBadge(
                        text = String.format(Locale.US, "%.1f%%", rates.withoutPrivate.ratePercent)
                    )
                }
                Text(
                    text = "${rates.withoutPrivate.successful} of ${rates.withoutPrivate.total} verified",
                    color = CampusGray
                )
            }
            Text(
                text = String.format(
                    Locale.US,
                    "Difference: %.1f points",
                    rates.withPrivate.ratePercent - rates.withoutPrivate.ratePercent
                ),
                style = MaterialTheme.typography.bodySmall,
                color = CampusGray
            )
        }
    }
}
