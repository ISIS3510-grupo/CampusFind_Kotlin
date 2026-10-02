package com.CampusFind.com.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.CampusFind.com.ui.components.CampusBorder
import com.CampusFind.com.ui.components.CampusGray
import com.CampusFind.com.ui.components.CampusYellow

@Composable
fun HomeScreen(
    onProfile: () -> Unit,
    onReportFound: () -> Unit,
    onLogout: () -> Unit
) {

    Scaffold(
        containerColor = Color.White,

        bottomBar = {

            NavigationBar(
                containerColor = Color.White
            ) {

                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = {
                        Text("Home")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    label = {
                        Text("Search")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Alerts"
                        )
                    },
                    label = {
                        Text("Alerts")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onProfile,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = {
                        Text("Profile")
                    }
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {

            /*
             * HEADER
             */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(105.dp)
                    .background(CampusYellow)
                    .padding(
                        start = 22.dp,
                        end = 12.dp,
                        top = 12.dp
                    ),
                verticalAlignment = Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "uniandes",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "Lost & Found",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "Find or return an item",
                        fontSize = 13.sp
                    )
                }

                IconButton(
                    onClick = onLogout
                ) {

                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Sign out",
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            /*
             * STUDENT ACTIONS
             */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {

                Text(
                    text = "What do you need?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                /*
                 * SEARCH FOUND ITEMS
                 *
                 * We will connect this later.
                 */
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(122.dp)
                        .border(
                            width = 1.dp,
                            color = CampusBorder,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            // Search Found Items will be connected later.
                        }
                        .padding(24.dp),
                    verticalAlignment = Alignment.Top
                ) {

                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(31.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(23.dp)
                    )

                    Column {

                        Text(
                            text = "Search found items",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Text(
                            text = "Check if something similar has already been registered.",
                            fontSize = 13.sp,
                            color = CampusGray
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                /*
                 * REPORT FOUND ITEM
                 */
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(104.dp)
                        .background(
                            color = CampusYellow,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            onReportFound()
                        }
                        .padding(24.dp),
                    verticalAlignment = Alignment.Top
                ) {

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(20.dp)
                    )

                    Column {

                        Text(
                            text = "I found an item",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Text(
                            text = "Report it and see where to deliver it.",
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(36.dp)
                )

                /*
                 * ACTIVE REPORT PLACEHOLDER
                 *
                 * Later Smart Matching will load the real
                 * active report from Firestore.
                 */
                Text(
                    text = "My active report",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(
                    modifier = Modifier.height(13.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .border(
                            width = 1.dp,
                            color = CampusBorder,
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "No active report loaded yet",
                        fontSize = 13.sp,
                        color = CampusGray
                    )
                }
            }
        }
    }
}