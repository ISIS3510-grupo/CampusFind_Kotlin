package com.CampusFind.com.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.CampusFind.com.ui.components.*

@Composable
fun MatchAlertScreen(
    onBack: () -> Unit = {},
    onReviewMatch: () -> Unit = {},
    onNotMine: () -> Unit = {}
) {
    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            BottomNavigationBar(
                selected = "Alerts"
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            ScreenTitle(
                title = "Possible match",
                subtitle = "We found an item that may be yours",
                onBack = onBack
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = CampusBorder,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(18.dp)
            ) {
                ImagePlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                Text(
                    text = "Scientific calculator",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Found in Mario Laserna · Today",
                    fontSize = 13.sp,
                    color = CampusGray
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                YellowBadge(
                    text = "Possible match"
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Review the public details. Ownership is confirmed later with staff.",
                    fontSize = 13.sp,
                    color = CampusGray
                )
            }

            Spacer(
                modifier = Modifier.height(34.dp)
            )

            PrimaryButton(
                text = "Review this match",
                onClick = onReviewMatch
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            SecondaryButton(
                text = "Not mine",
                onClick = onNotMine
            )
        }
    }
}