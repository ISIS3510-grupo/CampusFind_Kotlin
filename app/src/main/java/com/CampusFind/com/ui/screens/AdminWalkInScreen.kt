package com.CampusFind.com.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.CampusFind.com.ui.components.*
import com.CampusFind.com.viewmodel.AdminFoundItemDetailViewModel
import com.CampusFind.com.viewmodel.AdminWalkInViewModel
import java.text.DateFormat

@Composable
fun AdminWalkInScreen(
    foundItemId: String,
    isConnected: Boolean,
    onBack: () -> Unit,
    onDone: () -> Unit,
    detailViewModel: AdminFoundItemDetailViewModel = viewModel(),
    walkInViewModel: AdminWalkInViewModel = viewModel()
) {
    LaunchedEffect(foundItemId, isConnected) {
        if (isConnected) {
            detailViewModel.load(foundItemId)
        } else {
            detailViewModel.offline()
        }
    }

    val item = detailViewModel.item
    val enabled = isConnected &&
            item?.status == "available" &&
            !detailViewModel.isLoading &&
            !walkInViewModel.isBusy &&
            !walkInViewModel.delivered

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextButton(
                onClick = onBack,
                enabled = !walkInViewModel.isBusy
            ) {
                Text("‹ Back to search", color = Color.Black)
            }

            ScreenTitle(
                title = "Item details",
                subtitle = "Review the item and register a walk-in delivery",
                onBack = onBack
            )

            if (!isConnected) {
                Text(
                    "Reconnect to register a delivery.",
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (detailViewModel.isLoading) {
                CircularProgressIndicator(color = Color.Black)
            }

            detailViewModel.errorMessage?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            item?.let { found ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            CampusBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 84.dp, height = 108.dp)
                                .clip(RoundedCornerShape(9.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            val photo = detailViewModel.photo

                            if (photo != null) {
                                Image(
                                    bitmap = photo.asImageBitmap(),
                                    contentDescription = found.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                ImagePlaceholder(
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement =
                                Arrangement.spacedBy(7.dp)
                        ) {
                            Text(
                                found.title,
                                style = MaterialTheme.typography.titleMedium
                            )

                            YellowBadge(
                                found.status.replaceFirstChar {
                                    it.uppercase()
                                }
                            )

                            Text(
                                "Category: ${found.category}",
                                color = CampusGray
                            )

                            Text(
                                "Found: ${found.locationName}",
                                color = CampusGray
                            )

                            val date = found.createdAt
                                ?.toDate()?.let {
                                    DateFormat.getDateTimeInstance(
                                        DateFormat.MEDIUM,
                                        DateFormat.SHORT
                                    ).format(it)
                                } ?: "Date unavailable"

                            Text(date, color = CampusGray)
                        }
                    }

                    Text(
                        "Characteristics",
                        style = MaterialTheme.typography.titleSmall
                    )

                    Text(
                        if (isConnected) {
                            detailViewModel.privateDetail
                                ?.takeIf { it.isNotBlank() }
                                ?: "No private characteristics recorded."
                        } else {
                            "Private characteristics unavailable offline."
                        }
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    "Uniandes email",
                    style = MaterialTheme.typography.titleSmall
                )

                OutlinedTextField(
                    value = walkInViewModel.email,
                    onValueChange = walkInViewModel::updateEmail,
                    placeholder = {
                        Text("student@uniandes.edu.co")
                    },
                    singleLine = true,
                    enabled = enabled,
                    isError = walkInViewModel.email.isNotBlank() &&
                            !walkInViewModel.emailValid,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(9.dp)
                )

                Text(
                    "Physical identity verification",
                    style = MaterialTheme.typography.titleSmall
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = walkInViewModel.identityChecked,
                        onCheckedChange = walkInViewModel::updateIdentityChecked,
                        enabled = enabled
                    )

                    Text(
                        "I checked the physical Uniandes ID, " +
                                "confirmed the email, and verified " +
                                "the student owns the item.",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            walkInViewModel.error?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Button(
            onClick = {
                walkInViewModel.deliver(foundItemId)
            },
            enabled = enabled &&
                    walkInViewModel.emailValid &&
                    walkInViewModel.identityChecked,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .height(52.dp),
            shape = RoundedCornerShape(9.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            )
        ) {
            if (walkInViewModel.isBusy) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White
                )
            } else {
                Text("Create report & mark as delivered")
            }
        }
    }

    if (walkInViewModel.delivered) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Delivery recorded") },
            text = {
                Text(
                    "The walk-in delivery was registered successfully."
                )
            },
            confirmButton = {
                TextButton(onClick = onDone) {
                    Text("Done")
                }
            }
        )
    }
}
