package com.CampusFind.com.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
fun LostReportDetailScreen(
    matchCount: Int = 0,
    onBack: () -> Unit = {},
    onMatchClick: () -> Unit = {},
    onRecoveredElsewhere: () -> Unit = {}
) {
    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            BottomNavigationBar(
                selected = "Profile"
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
                title = "Lost report",
                subtitle = "Scientific calculator",
                onBack = onBack
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            YellowBadge(
                text =
                    if (matchCount == 1) {
                        "1 possible match"
                    } else {
                        "$matchCount possible matches"
                    }
            )

            Spacer(
                modifier = Modifier.height(18.dp)
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

                Text(
                    text = "Report details",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Text(
                    text = "Lost at Mario Laserna Building",
                    fontSize = 16.sp
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Sep 9 · 2:30 PM",
                    fontSize = 13.sp,
                    color = CampusGray
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Status updated 20 minutes ago",
                    fontSize = 13.sp,
                    color = CampusGray
                )
            }

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            if (matchCount > 0) {

                Text(
                    text =
                        if (matchCount == 1) {
                            "Possible match"
                        } else {
                            "Possible matches"
                        },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(116.dp)
                        .border(
                            width = 1.dp,
                            color = CampusBorder,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            onMatchClick()
                        }
                        .padding(10.dp)
                ) {

                    ImagePlaceholder(
                        modifier = Modifier
                            .width(92.dp)
                            .fillMaxHeight()
                    )

                    Spacer(
                        modifier = Modifier.width(16.dp)
                    )

                    Column {

                        Text(
                            text = "Scientific calculator",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "Found in ML · Today",
                            fontSize = 13.sp,
                            color = CampusGray
                        )

                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )

                        Text(
                            text = "View details →",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(28.dp)
                )
            }

            Text(
                text = "The report remains active until staff marks the item as delivered.",
                fontSize = 13.sp,
                color = CampusGray
            )

            Spacer(
                modifier = Modifier.height(40.dp)
            )

            SecondaryButton(
                text = "Mark recovered elsewhere",
                onClick = onRecoveredElsewhere
            )
        }
    }
}