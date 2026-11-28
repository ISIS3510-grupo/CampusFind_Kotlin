package com.CampusFind.com.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import java.text.DateFormat

import com.CampusFind.com.ui.components.CampusBorder
import com.CampusFind.com.ui.components.CampusGray
import com.CampusFind.com.ui.components.CampusYellow
import com.CampusFind.com.ui.components.ScreenTitle
import com.CampusFind.com.viewmodel.AdminFoundInventoryViewModel
import com.CampusFind.com.viewmodel.AdminLostReportsViewModel
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.BorderStroke


@Composable
fun AdminFoundInventoryScreen(
    isConnected: Boolean,
    onBack: () -> Unit,
    onDashboard: () -> Unit,
    onRegister: () -> Unit,
    onClaims: () -> Unit,
    viewModel: AdminFoundInventoryViewModel = viewModel(),
    lostViewModel: AdminLostReportsViewModel = viewModel()
) {

    var selectedTab by rememberSaveable {
        mutableStateOf("lost")
    }


    LaunchedEffect(isConnected) {
        if (isConnected) {
            viewModel.loadItems()
            lostViewModel.loadReports()
        }
    }

    val items = viewModel.items

    val isLoading = viewModel.isLoading

    val errorMessage = viewModel.errorMessage

    val searchQuery = viewModel.searchQuery.trim()

    val filteredLostReports = lostViewModel.reports.filter { report ->
        searchQuery.isBlank() ||
                report.title.contains(searchQuery, ignoreCase = true) ||
                report.category.contains(searchQuery, ignoreCase = true) ||
                report.locationName.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = false,
                    onClick = onDashboard,
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Dashboard") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Search, null) },
                    label = { Text("Search") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onRegister,
                    icon = { Icon(Icons.Default.Add, null) },
                    label = { Text("Register") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onClaims,
                    icon = { Icon(Icons.Default.Check, null) },
                    label = { Text("Claims") }
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {

            ScreenTitle(
                title = "Search records",
                subtitle = "Find lost reports or found inventory",
                onBack = onBack
            )

            if (!isConnected) {
                Text(
                    text = "No internet connection. Showing available data.",
                    color = CampusGray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = {
                    Text("Search item or location")
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = CampusBorder,
                    cursorColor = Color.Black
                )

            )

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        selectedTab = "lost"
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == "lost")
                            CampusYellow else Color.White,
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(1.dp, CampusBorder)
                ) {
                    Text("Matched Reports")
                }
                Button(
                    onClick = {
                        selectedTab = "found"
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == "found")
                            CampusYellow else Color.White,
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(1.dp, CampusBorder)
                ) {
                    Text("Found Inventory")
                }
            }

            Spacer(Modifier.height(20.dp))

            if (selectedTab == "lost") {

                if (lostViewModel.isLoading) {
                    Text(
                        text = "Loading matched reports...",
                        color = CampusGray
                    )
                }

                lostViewModel.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )

                    OutlinedButton(
                        onClick = {
                            lostViewModel.loadReports()
                        },
                        enabled = !lostViewModel.isLoading &&
                                lostViewModel.busyReportId == null
                    ) {
                        Text("Reload reports")
                    }
                }

                lostViewModel.successMessage?.let { message ->
                    Text(
                        text = message,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (
                    !lostViewModel.isLoading &&
                    lostViewModel.errorMessage == null &&
                    filteredLostReports.isEmpty()
                ) {
                    Text(
                        text = "No matched reports",
                        color = CampusGray
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {

                    items(
                        filteredLostReports,
                        key = { it.id }
                    ) { report ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    CampusBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(78.dp)
                                    .background(
                                        CampusBorder,
                                        RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = CampusGray,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {

                                Text(
                                    text = report.title.ifBlank {
                                        "Untitled report"
                                    },
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )

                                Text(
                                    text = "Category: ${report.category}",
                                    color = CampusGray
                                )

                                Text(
                                    text = report.locationName.ifBlank {
                                        "Location unavailable"
                                    },
                                    color = CampusGray
                                )

                                Text(
                                    text = report.reportedAt?.toDate()?.let {
                                        DateFormat.getDateInstance(
                                            DateFormat.MEDIUM
                                        ).format(it)
                                    } ?: "Date unavailable",
                                    color = CampusGray
                                )
                            }
                        }
                    }
                }

            } else {

                if (isLoading) {
                    Text(
                        text = "Updating inventory...",
                        color = CampusGray
                    )
                }

                if (errorMessage != null) {
                    Text(
                        text = "Could not update inventory: $errorMessage",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                if (items.isEmpty() && !isLoading && errorMessage == null) {
                    Text(
                        text = if (isConnected) {
                            "No found items to display"
                        } else {
                            "No items loaded yet. Connect to the internet to load the inventory."
                        },
                        color = CampusGray
                    )
                }

                if (items.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 20.dp)
                    ) {
                        items(items, key = { it.id }) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        1.dp,
                                        CampusBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(78.dp)
                                        .background(
                                            CampusBorder,
                                            RoundedCornerShape(10.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = null,
                                        tint = CampusGray,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = item.title.ifBlank {
                                            "Untitled item"
                                        },
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black
                                    )

                                    Text(
                                        text = item.locationName.ifBlank {
                                            "Location unavailable"
                                        },
                                        color = CampusGray
                                    )

                                    Text(
                                        text = item.createdAt?.toDate()?.let {
                                            DateFormat.getDateInstance(
                                                DateFormat.MEDIUM
                                            ).format(it)
                                        } ?: "Date unavailable",
                                        color = CampusGray
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }
        }
    }