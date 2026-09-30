package com.CampusFind.com.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.CampusFind.com.ui.components.*
import com.CampusFind.com.ui.theme.AnaheimFontFamily

@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    onReportLost: () -> Unit = {},
    onOpenReport: () -> Unit = {}
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
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CampusYellow)
                    .padding(
                        horizontal = 22.dp,
                        vertical = 12.dp
                    )
            ) {
                Text(
                    text = "uniandes",
                    fontSize = 25.sp ,
                    fontWeight = FontWeight.Medium,
                    fontFamily = AnaheimFontFamily
                )

                ScreenTitle(
                    title = "Profile",
                    subtitle = "Nicolas Martinez · Student",
                    onBack = onBack,
                    titleSize = 25.sp
                )
            }

            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(92.dp)
                        .border(
                            width = 1.dp,
                            color = CampusBorder,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(31.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(18.dp)
                    )

                    Column {
                        Text(
                            text = "n.martinez@uniandes.edu.co",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "Authenticated with Uniandes",
                            fontSize = 13.sp,
                            color = CampusGray
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(28.dp)
                )

                PrimaryButton(
                    text = "Report a lost item",
                    onClick = onReportLost,
                    height = 54
                )

                Spacer(
                    modifier = Modifier.height(34.dp)
                )

                Text(
                    text = "My lost-item reports",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                ReportCard(
                    title = "Scientific calculator",
                    subtitle = "Reported Sep 9 · ML",
                    badge = "Possible match",
                    onClick = onOpenReport
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                ReportCard(
                    title = "Laptop charger",
                    subtitle = "Reported Aug 28 · Library",
                    badge = "Open"
                )
            }
        }
    }
}

@Composable
private fun ReportCard(
    title: String,
    subtitle: String,
    badge: String,
    onClick: () -> Unit = {}
) {
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
                onClick()
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
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = CampusGray
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            YellowBadge(
                text = badge
            )
        }
    }
}