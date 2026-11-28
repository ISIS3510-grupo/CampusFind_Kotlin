package com.CampusFind.com.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.CampusFind.com.ui.components.*
import com.CampusFind.com.viewmodel.AdminFoundItemDetailViewModel
import java.text.DateFormat
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke

@Composable
fun AdminFoundItemDetailScreen(
    foundItemId: String,
    isConnected: Boolean,
    onBack: () -> Unit,
    onClaimReview: () -> Unit,
    onWalkIn: () -> Unit,
    viewModel: AdminFoundItemDetailViewModel = viewModel()
) {
    LaunchedEffect(foundItemId, isConnected) {
        if (isConnected) {
            viewModel.load(foundItemId)
        } else {
            viewModel.offline()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        ScreenTitle(
            title = "Found inventory item",
            subtitle = viewModel.item?.let {
                "${it.status.replaceFirstChar { char ->
                    char.uppercase()
                }} · ${it.title}"
            } ?: "Item details",
            onBack = onBack,
            titleSize = 28.sp
        )

        if (viewModel.isLoading) {
            CircularProgressIndicator(color = Color.Black)
        }

        viewModel.errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }

        viewModel.item?.let { item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(185.dp)
                    .background(
                        CampusBorder,
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.photo != null) {
                    Image(
                        bitmap = viewModel.photo!!.asImageBitmap(),
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    ImagePlaceholder(
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Text(
                text = item.title.ifBlank { "Untitled item" },
                style = MaterialTheme.typography.headlineSmall
            )

            YellowBadge(
                item.status.ifBlank { "Unknown" }
                    .replaceFirstChar { it.uppercase() }
            )

            Text(
                text = "Found at",
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = "${item.locationName.ifBlank {
                    "Location unavailable"
                }} · " + (
                        item.createdAt?.toDate()?.let {
                            DateFormat.getDateInstance(
                                DateFormat.MEDIUM
                            ).format(it)
                        } ?: "Date unavailable"
                        )
            )

            Text(
                text = "Private ownership detail",
                style = MaterialTheme.typography.titleSmall
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        CampusBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null
                )

                Text(
                    text = if (isConnected) {
                        viewModel.privateDetail
                            ?.takeIf { it.isNotBlank() }
                            ?: "No private detail recorded."
                    } else {
                        "Private detail unavailable offline."
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Button(
                onClick = onClaimReview,
                enabled = isConnected && !viewModel.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                shape = RoundedCornerShape(9.dp)
            ) {
                Text(
                    "Open claim review",
                    color = Color.White
                )
            }

            OutlinedButton(
                onClick = onWalkIn,
                enabled = isConnected && !viewModel.isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Black
                ),
                border = BorderStroke(1.5.dp, Color.Black)
            ) {
                Text("Register walk-in delivery")
            }
        }
    }
}

