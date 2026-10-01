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
import com.CampusFind.com.data.ClaimsRepository
import com.CampusFind.com.model.ClaimReview
import java.util.Locale

@Composable
fun AdminClaimsScreen(onBack: () -> Unit) {
    val repository = remember { ClaimsRepository() }
    var claims by remember { mutableStateOf(emptyList<ClaimReview>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(repository) {
        repository.getPendingClaims(
            onSuccess = { result ->
                claims = result
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
            text = "Review claims",
            style = MaterialTheme.typography.headlineSmall
        )

        if (isLoading) {
            Text(text = "Loading...")
        } else if (errorMessage != null) {
            Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)
        } else if (claims.isEmpty()) {
            Text(text = "No pending claims")
        } else {
            for (claim in claims) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Report: ${claim.reportId}")
                        Text(text = "Student answer: ${claim.answer}")
                        Text(text = "Registered: ${claim.registered}")
                        Text(text = String.format(Locale.US, "Score: %.0f%%", claim.score * 100))
                        Text(text = claim.verdict.name)
                    }
                }
            }
        }

        Button(onClick = onBack) {
            Text(text = "Back")
        }
    }
}
