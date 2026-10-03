package com.CampusFind.com.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.CampusFind.com.ui.components.CampusBorder
import com.CampusFind.com.ui.components.CampusGray
import com.CampusFind.com.ui.components.CampusYellow
import com.CampusFind.com.ui.components.PrimaryButton
import com.CampusFind.com.ui.components.SecondaryButton
import com.CampusFind.com.ui.theme.AnaheimFontFamily

@Composable
fun AdminHomeScreen(
    email: String,
    onRegisterFound: () -> Unit,
    onReportTimes: () -> Unit,
    onMatchSearchTime: () -> Unit,
    onDonationPotential: () -> Unit,
    onReviewClaims: () -> Unit,
    onOwnershipRates: () -> Unit,
    onReportBottleneck: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Sign out",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 20.dp,
                    vertical = 18.dp
                )
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    10.dp
                )
            ) {
                DashboardMetricCard(
                    value = "0",
                    label = "New found",
                    highlighted = true,
                    modifier = Modifier.weight(1f)
                )

                DashboardMetricCard(
                    value = "0",
                    label = "Matches",
                    modifier = Modifier.weight(1f)
                )

                DashboardMetricCard(
                    value = "0",
                    label = "Claims",
                    modifier = Modifier.weight(1f)
                )
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

            PrimaryButton(
                text = "Register found item",
                onClick = onRegisterFound
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            SecondaryButton(
                text = "Search lost & found records",
                onClick = {}
            )

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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = CampusBorder,
                        shape = RoundedCornerShape(
                            10.dp
                        )
                    )
                    .padding(14.dp)
            ) {
                Text(
                    text = "No pending claims loaded",
                    fontSize = 15.sp,
                    color = CampusGray
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Box(
                    modifier = Modifier.width(
                        110.dp
                    )
                ) {
                    SecondaryButton(
                        text = "Review",
                        onClick = onReviewClaims,
                        height = 40
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = CampusBorder
                )
                .background(Color.White)
                .padding(
                    vertical = 8.dp
                ),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AdminBottomNavItem(
                icon = Icons.Default.Home,
                label = "Dashboard",
                selected = true,
                onClick = {}
            )

            AdminBottomNavItem(
                icon = Icons.Default.Search,
                label = "Search",
                selected = false,
                onClick = {}
            )

            AdminBottomNavItem(
                icon = Icons.Default.Add,
                label = "Register",
                selected = false,
                onClick = onRegisterFound
            )

            AdminBottomNavItem(
                icon = Icons.Default.Check,
                label = "Claims",
                selected = false,
                onClick = onReviewClaims
            )
        }
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
private fun AdminBottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color =
        if (selected)
            Color.Black
        else
            CampusGray

    Column(
        modifier = Modifier
            .width(72.dp)
            .clickable {
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(
                22.dp
            )
        )

        Spacer(
            modifier = Modifier.height(
                3.dp
            )
        )

        Text(
            text = label,
            fontSize = 10.sp,
            color = color
        )
    }
}