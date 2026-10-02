package com.CampusFind.com.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.CampusFind.com.ui.components.CampusGray
import com.CampusFind.com.ui.components.CampusYellow
import com.CampusFind.com.ui.components.PrimaryButton
import com.CampusFind.com.ui.components.ScreenTitle
import com.CampusFind.com.ui.components.SecondaryButton
import com.CampusFind.com.ui.theme.AnaheimFontFamily

@Composable
fun AdminHomeScreen(
    email: String,
    onRegisterFound: () -> Unit,
    onReportTimes: () -> Unit,
    onDonationPotential: () -> Unit,
    onReviewClaims: () -> Unit,
    onOwnershipRates: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CampusYellow)
                .statusBarsPadding()
                .padding(horizontal = 22.dp, vertical = 12.dp)
        ) {
            Text(
                text = "uniandes",
                fontSize = 25.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = AnaheimFontFamily
            )

            ScreenTitle(
                title = "Admin Dashboard",
                subtitle = "Lost Items Service",
                titleSize = 25.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = email,
                fontSize = 13.sp,
                color = CampusGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            ScreenTitle(
                title = "Quick actions",
                titleSize = 25.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                PrimaryButton(
                    text = "Register found item",
                    onClick = onRegisterFound
                )
                SecondaryButton(
                    text = "Average time to found",
                    onClick = onReportTimes
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

            Spacer(modifier = Modifier.height(32.dp))

            SecondaryButton(
                text = "Sign out",
                onClick = onLogout
            )
        }
    }
}
