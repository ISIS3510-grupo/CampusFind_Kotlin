package com.CampusFind.com.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.CampusFind.com.ui.components.BottomNavigationBar
import com.CampusFind.com.ui.components.CampusBorder
import com.CampusFind.com.ui.components.CampusGray
import com.CampusFind.com.ui.components.CampusYellow
import com.CampusFind.com.ui.components.ImagePlaceholder
import com.CampusFind.com.ui.components.PrimaryButton
import com.CampusFind.com.ui.components.ScreenTitle
import com.CampusFind.com.ui.components.YellowBadge
import com.CampusFind.com.ui.theme.AnaheimFontFamily
import com.CampusFind.com.viewmodel.PossibleMatchesViewModel

@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    onReportLost: () -> Unit = {},
    onOpenReport: (String) -> Unit = {}
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
                .background(
                    Color.White
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        CampusYellow
                    )
                    .padding(
                        horizontal = 22.dp,
                        vertical = 12.dp
                    )
            ) {

                Text(
                    text = "uniandes",
                    fontSize = 25.sp,
                    fontWeight =
                        FontWeight.Medium,
                    fontFamily =
                        AnaheimFontFamily
                )

                ScreenTitle(
                    title = "Profile",
                    subtitle =
                        "Nicolas Martinez · Student",
                    onBack = onBack,
                    titleSize = 25.sp
                )
            }

            Column(
                modifier =
                    Modifier.padding(
                        24.dp
                    )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            92.dp
                        )
                        .border(
                            width = 1.dp,
                            color =
                                CampusBorder,
                            shape =
                                RoundedCornerShape(
                                    10.dp
                                )
                        )
                        .padding(
                            18.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Person,

                        contentDescription =
                            null,

                        modifier =
                            Modifier
                                .width(
                                    31.dp
                                )
                                .height(
                                    31.dp
                                )
                    )

                    Spacer(
                        modifier =
                            Modifier.width(
                                18.dp
                            )
                    )

                    Column {

                        Text(
                            text =
                                "n.martinez@uniandes.edu.co",

                            fontSize =
                                16.sp,

                            fontWeight =
                                FontWeight.Medium
                        )

                        Spacer(
                            modifier =
                                Modifier.height(
                                    8.dp
                                )
                        )

                        Text(
                            text =
                                "Authenticated with Uniandes",

                            fontSize =
                                13.sp,

                            color =
                                CampusGray
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(
                            28.dp
                        )
                )

                PrimaryButton(
                    text =
                        "Report a lost item",

                    onClick =
                        onReportLost,

                    height = 54
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            34.dp
                        )
                )

                Text(
                    text =
                        "My lost-item reports",

                    fontSize =
                        20.sp,

                    fontWeight =
                        FontWeight.Medium
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            16.dp
                        )
                )

                /*
                 * Estos reportes todavía son datos visuales
                 * de prueba del Profile.
                 *
                 * Cuando Profile lea lostReports desde
                 * Firestore, el reportId simplemente será:
                 *
                 * report.id
                 */
                ReportCard(
                    reportId =
                        "lost_test_001",

                    title =
                        "Scientific calculator",

                    subtitle =
                        "Reported Sep 9 · ML",

                    onClick =
                        onOpenReport
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            16.dp
                        )
                )

                ReportCard(
                    reportId =
                        "lost_test_002",

                    title =
                        "Laptop charger",

                    subtitle =
                        "Reported Aug 28 · Library",

                    onClick =
                        onOpenReport
                )
            }
        }
    }
}

@Composable
private fun ReportCard(
    reportId: String,
    title: String,
    subtitle: String,
    onClick: (String) -> Unit = {}
) {

    /*
     * Cada reporte observa únicamente los matches
     * asociados a su propio reportId.
     *
     * Esto responde la BQ Type 2:
     * "How many possible matches are found for a
     * student's lost-item report?"
     */
    val possibleMatchesViewModel:
            PossibleMatchesViewModel =
        viewModel(
            key = "matches_$reportId"
        )

    LaunchedEffect(
        reportId
    ) {
        possibleMatchesViewModel
            .observeMatches(
                reportId = reportId
            )
    }

    val matchCount =
        possibleMatchesViewModel
            .matchCount

    val badgeText =
        if (matchCount == 1) {
            "1 possible match"
        } else {
            "$matchCount possible matches"
        }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                116.dp
            )
            .border(
                width = 1.dp,
                color =
                    CampusBorder,
                shape =
                    RoundedCornerShape(
                        10.dp
                    )
            )
            .clickable {

                onClick(
                    reportId
                )
            }
            .padding(
                10.dp
            )
    ) {

        ImagePlaceholder(
            modifier =
                Modifier
                    .width(
                        92.dp
                    )
                    .fillMaxHeight()
        )

        Spacer(
            modifier =
                Modifier.width(
                    16.dp
                )
        )

        Column {

            Text(
                text =
                    title,

                fontSize =
                    16.sp,

                fontWeight =
                    FontWeight.Medium
            )

            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )

            Text(
                text =
                    subtitle,

                fontSize =
                    13.sp,

                color =
                    CampusGray
            )

            Spacer(
                modifier =
                    Modifier.height(
                        14.dp
                    )
            )

            YellowBadge(
                text =
                    badgeText
            )
        }
    }
}