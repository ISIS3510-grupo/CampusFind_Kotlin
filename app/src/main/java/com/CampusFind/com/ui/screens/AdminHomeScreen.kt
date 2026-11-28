package com.CampusFind.com.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.CampusFind.com.model.AdminPendingClaim
import com.CampusFind.com.service.RelativeTimeFormatter
import com.CampusFind.com.ui.components.AdminBottomNav
import com.CampusFind.com.ui.components.AdminNavDestination
import com.CampusFind.com.ui.components.CampusBorder
import com.CampusFind.com.ui.components.CampusGray
import com.CampusFind.com.ui.components.CampusYellow
import com.CampusFind.com.ui.components.PrimaryButton
import com.CampusFind.com.ui.components.SecondaryButton
import com.CampusFind.com.ui.theme.AnaheimFontFamily
import com.CampusFind.com.viewmodel.AdminDashboardViewModel

@Composable
fun AdminHomeScreen(
    email: String,
    onRegisterFound: () -> Unit,
    onSearchRecords: () -> Unit,
    onReportTimes: () -> Unit,
    onFoundCategories: () -> Unit,
    onMatchSearchTime: () -> Unit,
    onDonationPotential: () -> Unit,
    onReviewClaims: () -> Unit,
    onOwnershipRates: () -> Unit,
    onLogout: () -> Unit,
    viewModel: AdminDashboardViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CampusYellow)
                    .statusBarsPadding()
                    .padding(
                        start = 20.dp,
                        end = 12.dp,
                        top = 10.dp,
                        bottom = 10.dp
                    ),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "uniandes",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = AnaheimFontFamily,
                        color = Color.Black
                    )

                    Text(
                        text = "Admin dashboard",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = AnaheimFontFamily,
                        color = Color.Black
                    )

                    Text(
                        text = "Lost Items Service",
                        fontSize = 13.sp,
                        color = Color.Black,
                        fontFamily = AnaheimFontFamily
                    )
                }

                IconButton(
                    onClick = onLogout
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Sign out",
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Text(
                    text = "Today",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                val metrics = viewModel.metrics
                when {
                    viewModel.isLoadingMetrics -> Text("Loading today's metrics...", color = CampusGray)
                    viewModel.metricsError != null -> Text(
                        text = viewModel.metricsError.orEmpty(),
                        color = MaterialTheme.colorScheme.error
                    )
                    metrics != null -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(
                                10.dp
                            )
                        ) {
                            DashboardMetricCard(
                                value = metrics.newFound.toString(),
                                label = "New found",
                                highlighted = true,
                                modifier = Modifier.weight(1f)
                            )

                            DashboardMetricCard(
                                value = metrics.matches.toString(),
                                label = "Matches",
                                modifier = Modifier.weight(1f)
                            )

                            DashboardMetricCard(
                                value = metrics.claims.toString(),
                                label = "Claims",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(28.dp)
                )

                Text(
                    text = "Quick actions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PrimaryButton(
                        text = "Register found item",
                        onClick = onRegisterFound
                    )

                    SecondaryButton(
                        text = "Search lost & found records",
                        onClick = onSearchRecords
                    )

                    SecondaryButton(
                        text = "Average time to found",
                        onClick = onReportTimes
                    )

                    SecondaryButton(
                        text = "Found items by category",
                        onClick = onFoundCategories
                    )

                    SecondaryButton(
                        text = "Match search performance",
                        onClick = onMatchSearchTime
                    )

                    SecondaryButton(
                        text = "Donation potential",
                        onClick = onDonationPotential
                    )

                    SecondaryButton(
                        text = "Review claims",
                        onClick = onReviewClaims
                    )

                    SecondaryButton(
                        text = "Verification rate",
                        onClick = onOwnershipRates
                    )
                }

                Spacer(
                    modifier = Modifier.height(28.dp)
                )

                Text(
                    text = "Pending claims",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                when {
                    viewModel.isLoadingClaims -> Text("Loading pending claims...", color = CampusGray)
                    viewModel.claimsError != null -> Text(
                        text = viewModel.claimsError.orEmpty(),
                        color = MaterialTheme.colorScheme.error
                    )
                    viewModel.pendingClaims.isEmpty() -> Text("No pending claims", color = CampusGray)
                    else -> {
                        val now = System.currentTimeMillis()
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            for (claim in viewModel.pendingClaims) {
                                PendingClaimCard(claim, now, onReviewClaims)
                            }
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )
            }
        }

        AdminBottomNav(
            selected = AdminNavDestination.Dashboard,
            onDashboard = {},
            onSearch = onSearchRecords,
            onRegister = onRegisterFound,
            onClaims = onReviewClaims
        )
    }
}

@Composable
private fun DashboardMetricCard(
    value: String,
    label: String,
    highlighted: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(84.dp)
            .background(
                color =
                    if (highlighted)
                        CampusYellow
                    else
                        Color.White,
                shape = RoundedCornerShape(
                    10.dp
                )
            )
            .border(
                width = 1.dp,
                color = CampusBorder,
                shape = RoundedCornerShape(
                    10.dp
                )
            )
            .padding(
                horizontal = 10.dp,
                vertical = 10.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = value,
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(
            modifier = Modifier.height(
                2.dp
            )
        )

        Text(
            text = label,
            fontSize = 12.sp,
            color = CampusGray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PendingClaimCard(
    claim: AdminPendingClaim,
    nowMillis: Long,
    onReview: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CampusBorder, RoundedCornerShape(10.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(CampusBorder, RoundedCornerShape(8.dp))
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = claim.itemTitle, fontWeight = FontWeight.Bold)
            val relativeTime = RelativeTimeFormatter.format(claim.createdAtMillis, nowMillis)
            Text(text = "${claim.studentName} · $relativeTime", color = CampusGray, fontSize = 13.sp)
            SecondaryButton(
                text = "Review",
                onClick = onReview,
                modifier = Modifier.width(110.dp),
                height = 40
            )
        }
    }
}
