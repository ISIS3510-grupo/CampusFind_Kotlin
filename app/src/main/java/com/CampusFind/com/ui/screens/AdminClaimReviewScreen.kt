package com.CampusFind.com.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.CampusFind.com.ui.components.*
import com.CampusFind.com.viewmodel.AdminClaimReviewViewModel

@Composable
fun AdminClaimReviewScreen(
    reportId: String?,
    foundItemId: String?,
    walkInInitially: Boolean,
    isConnected: Boolean,
    onBack: () -> Unit,
    viewModel: AdminClaimReviewViewModel = viewModel()
) {
    var walkIn by rememberSaveable(reportId, foundItemId) {
        mutableStateOf(walkInInitially)
    }

    var reportText by rememberSaveable(reportId, foundItemId) {
        mutableStateOf(reportId.orEmpty())
    }

    var email by rememberSaveable {
        mutableStateOf("")
    }

    var expanded by remember { mutableStateOf(false) }

    val claim = viewModel.selectedClaim
    val itemId = foundItemId ?: claim?.foundItemId

    var ownership by remember(claim?.id, walkIn, reportText, email) {
        mutableStateOf(false)
    }

    var identity by remember(claim?.id, walkIn, reportText, email) {
        mutableStateOf(false)
    }

    val enabled = isConnected &&
            !viewModel.loading &&
            !viewModel.busy

    LaunchedEffect(reportId, foundItemId, isConnected) {
        if (!isConnected) {
            viewModel.offline()
        } else if (walkInInitially && reportId == null) {
            viewModel.clear()
        } else {
            viewModel.loadClaims(reportId, foundItemId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ScreenTitle(
            title = "Claim review",
            subtitle = "Final handoff is always in person",
            onBack = onBack
        )

        if (!isConnected) {
            Text(
                "Reconnect to review claims.",
                color = MaterialTheme.colorScheme.error
            )
        }

        if (viewModel.loading) {
            CircularProgressIndicator(color = Color.Black)
        }

        viewModel.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        if (enabled && viewModel.error != null) {
            TextButton(
                onClick = {
                    if (walkIn && itemId != null &&
                        reportText.isNotBlank()
                    ) {
                        viewModel.loadWalkIn(reportText.trim(), itemId)
                    } else {
                        viewModel.loadClaims(reportId, foundItemId)
                    }
                }
            ) {
                Text("Retry")
            }
        }

        if (isConnected && !viewModel.loading) {
            if (viewModel.delivered) {
                Text("Physical delivery has been recorded.")

                AdminButton(
                    "Finalize and close report",
                    enabled && itemId != null &&
                            (if (walkIn) reportText.isNotBlank()
                            else claim != null)
                ) {
                    val currentReport = if (walkIn) {
                        reportText.trim()
                    } else {
                        claim?.reportId.orEmpty()
                    }

                    if (itemId != null) {
                        viewModel.close(currentReport, itemId, onBack)
                    }
                }

            } else if (walkIn) {
                Text(
                    "Register walk-in delivery",
                    style = MaterialTheme.typography.titleLarge
                )

                OutlinedTextField(
                    value = reportText,
                    onValueChange = {
                        reportText = it
                        viewModel.clearEvidence()
                    },
                    label = { Text("Lost report ID") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enabled
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Student's institutional email") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enabled
                )

                OutlinedButton(
                    onClick = {
                        itemId?.let {
                            viewModel.loadWalkIn(reportText.trim(), it)
                        }
                    },
                    enabled = enabled &&
                            itemId != null &&
                            reportText.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Load ownership details")
                }

                viewModel.evidence?.let { proof ->
                    DetailBox {
                        Text("Found: ${proof.itemTitle}")
                        Text("Lost: ${proof.reportTitle}")
                        DetailText(
                            "Private item detail",
                            proof.foundPrivateDetail
                        )
                        DetailText(
                            "Report's private detail",
                            proof.reportPrivateDetail
                        )
                    }

                    Verification(
                        ownership, identity, enabled,
                        { ownership = it },
                        { identity = it }
                    )

                    AdminButton(
                        "Verify ID & mark as delivered",
                        enabled && ownership && identity &&
                                email.trim().endsWith(
                                    "@uniandes.edu.co",
                                    ignoreCase = true
                                )
                    ) {
                        itemId?.let {
                            viewModel.walkIn(
                                reportText.trim(),
                                it,
                                email.trim(),
                                ownership,
                                identity
                            )
                        }
                    }
                }

            } else {
                if (viewModel.claims.isEmpty()) {
                    Text(
                        "No pending or active claims.",
                        color = CampusGray
                    )
                }

                if (viewModel.claims.size > 1) {
                    Box {
                        OutlinedButton(
                            onClick = { expanded = true },
                            enabled = enabled
                        ) {
                            Text("Claim: ${claim?.claimantName}")
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            viewModel.claims.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(option.claimantName)
                                    },
                                    onClick = {
                                        viewModel.selectClaim(option.id)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                claim?.let { selected ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                CampusYellow,
                                RoundedCornerShape(10.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text("Claimant")
                        Text(
                            selected.claimantName,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(selected.claimantEmail)
                        Text("Digital ownership answers submitted")
                    }

                    Text(
                        "Found item and lost report",
                        style = MaterialTheme.typography.titleMedium
                    )

                    DetailBox {
                        Text("Found: ${selected.itemTitle}")
                        Text("Lost: ${selected.reportTitle}")
                        Text("Status: ${selected.status}")
                    }

                    Text(
                        "Compare claim answers",
                        style = MaterialTheme.typography.titleMedium
                    )

                    DetailBox {
                        DetailText(
                            "Student's answer",
                            selected.ownershipAnswer
                        )
                        DetailText(
                            "Private item detail",
                            selected.foundPrivateDetail
                        )
                        DetailText(
                            "Report's private detail",
                            selected.reportPrivateDetail
                        )
                        Text("Staff must verify ownership independently.")
                    }

                    Verification(
                        ownership, identity, enabled,
                        { ownership = it },
                        { identity = it }
                    )

                    if (selected.status == "pending") {
                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.review(
                                        false, ownership,
                                        reportId, foundItemId
                                    )
                                },
                                enabled = enabled,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Reject claim")
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.review(
                                        true, ownership,
                                        reportId, foundItemId
                                    )
                                },
                                enabled = enabled && ownership,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Approve ownership")
                            }
                        }
                    }

                    AdminButton(
                        "Verify ID & mark as delivered",
                        enabled && ownership && identity &&
                                selected.status in listOf(
                            "pending", "approved"
                        )
                    ) {
                        viewModel.deliver(
                            ownership, identity,
                            reportId, foundItemId
                        )
                    }

                    if (selected.status == "pending") {
                        OutlinedButton(
                            onClick = {
                                reportText = selected.reportId
                                viewModel.clearEvidence()
                                walkIn = true
                            },
                            enabled = enabled,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Register walk-in delivery instead")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailBox(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                CampusBorder,
                RoundedCornerShape(10.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = content
    )
}

@Composable
private fun DetailText(label: String, value: String?) {
    Text("$label: ${value?.takeIf { it.isNotBlank() } ?: "Not recorded"}")
}

@Composable
private fun Verification(
    ownership: Boolean,
    identity: Boolean,
    enabled: Boolean,
    onOwnership: (Boolean) -> Unit,
    onIdentity: (Boolean) -> Unit
) {
    Text(
        "Ownership and identity verification",
        style = MaterialTheme.typography.titleMedium
    )

    DetailBox {
        Row {
            Checkbox(
                checked = ownership,
                onCheckedChange = onOwnership,
                enabled = enabled
            )
            Text(
                "I verified the private ownership details.",
                modifier = Modifier.weight(1f).padding(top = 12.dp)
            )
        }

        Row {
            Checkbox(
                checked = identity,
                onCheckedChange = onIdentity,
                enabled = enabled
            )
            Text(
                "I physically verified the Uniandes ID and institutional email.",
                modifier = Modifier.weight(1f).padding(top = 12.dp)
            )
        }
    }

    Text(
        "Only mark delivered after the physical handoff.",
        color = CampusGray
    )
}

@Composable
private fun AdminButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black
        )
    ) {
        Text(label, color = Color.White)
    }
}

