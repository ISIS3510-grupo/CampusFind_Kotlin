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
import androidx.compose.ui.unit.sp
import com.CampusFind.com.data.ClaimsRepository
import com.CampusFind.com.model.ClaimReview
import com.CampusFind.com.ui.components.CampusBorder
import com.CampusFind.com.ui.components.CampusGray
import com.CampusFind.com.ui.components.ScreenTitle
import com.CampusFind.com.ui.components.YellowBadge
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
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ScreenTitle(
            title = "Review claims",
            subtitle = "Compare the student's answer with the registered details",
            onBack = onBack
        )

        if (isLoading) {
            Text(text = "Loading...", color = CampusGray)
        } else if (errorMessage != null) {
            Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)
        } else if (claims.isEmpty()) {
            Text(text = "No pending claims", color = CampusGray)
        } else {
            for (claim in claims) {
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
                            text = "Report: ${claim.reportId}",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        YellowBadge(
                            text = String.format(Locale.US, "%.0f%%", claim.score * 100)
                        )
                    }
                    Text(text = "Student answer", color = CampusGray, fontSize = 13.sp)
                    Text(text = claim.answer)
                    Text(text = "Registered details", color = CampusGray, fontSize = 13.sp)
                    Text(text = claim.registered)
                    Text(text = claim.verdict.displayName, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
