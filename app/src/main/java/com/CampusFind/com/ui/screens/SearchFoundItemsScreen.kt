
package com.CampusFind.com.ui.screens

import android.app.Application
import android.text.format.DateUtils
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.CampusFind.com.model.SearchFoundItem
import com.CampusFind.com.ui.components.CampusBorder
import com.CampusFind.com.ui.components.CampusGray
import com.CampusFind.com.ui.components.CampusYellow
import com.CampusFind.com.viewmodel.SearchFoundItemsViewModel

@Composable
fun SearchFoundItemsScreen(
    isConnected: Boolean,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onAlerts: () -> Unit,
    onProfile: () -> Unit
) {
    val context = LocalContext.current

    val viewModel: SearchFoundItemsViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory
            .getInstance(context.applicationContext as Application)
    )

    // Load saved items and refresh when connectivity changes.
    LaunchedEffect(isConnected) {
        viewModel.loadItems(isConnected)
    }

    val foundItems = viewModel.items

    // Selected item for the details popup.
    var selectedItem by remember {
        mutableStateOf<SearchFoundItem?>(null)
    }

    Scaffold(
        containerColor = Color.White,

        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {

                NavigationBarItem(
                    selected = false,
                    onClick = onHome,
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = {
                        Text("Home")
                    }
                )

                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    label = {
                        Text("Search")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onAlerts,
                    icon = {
                        Icon(
                            Icons.Default.Notifications,
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
                            Icons.Default.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = {
                        Text("Profile")
                    }
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .padding(
                    horizontal = 16.dp,
                    vertical = 18.dp
                )
        ) {

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Found items",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Text(
                        text = "Search before visiting the office",
                        fontSize = 12.sp,
                        color = CampusGray
                    )
                }

                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }

            Spacer(
                Modifier.height(16.dp)
            )

            // Search field
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder = {
                    Text(
                        "Search by item or keyword",
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(9.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = CampusBorder
                )
            )

            Spacer(
                Modifier.height(12.dp)
            )

            // Horizontal category filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(
                        rememberScrollState()
                    ),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                viewModel.availableCategories.forEach { category ->

                    FilterChip(
                        selected =
                            viewModel.selectedCategory == category,

                        onClick = {
                            viewModel.onCategorySelected(category)
                        },

                        label = {
                            Text(
                                category.replaceFirstChar { it.titlecase() },
                                fontSize = 12.sp
                            )
                        },

                        shape = RoundedCornerShape(10.dp),

                        colors = FilterChipDefaults
                            .filterChipColors(
                                containerColor = Color.White,
                                selectedContainerColor = CampusYellow,
                                selectedLabelColor = Color.Black
                            )
                    )
                }
            }

            // Offline state
            if (!isConnected) {

                Spacer(
                    Modifier.height(10.dp)
                )

                Text(
                    text = if (viewModel.hasSavedData) {
                        "Offline — showing saved items"
                    } else {
                        "Offline — no saved items available yet"
                    },
                    color = CampusGray,
                    fontSize = 12.sp
                )
            }

            // Loading state
            if (viewModel.isLoading) {

                Spacer(
                    Modifier.height(8.dp)
                )

                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = CampusYellow
                )
            }

            // Error state
            viewModel.errorMessage?.let { error ->

                Spacer(
                    Modifier.height(8.dp)
                )

                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

            Spacer(
                Modifier.height(14.dp)
            )

            // Found items list with vertical scrolling
            if (
                foundItems.isEmpty() &&
                !viewModel.isLoading
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = if (
                            !isConnected &&
                            !viewModel.hasSavedData
                        ) {
                            "No saved items available offline."
                        } else {
                            "No found items match your search."
                        },
                        color = CampusGray
                    )
                }

            } else {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp),
                    contentPadding =
                        PaddingValues(bottom = 16.dp)
                ) {

                    items(
                        items = foundItems
                    ) { item ->

                        SearchFoundItemCard(
                            item = item,
                            onClick = {
                                selectedItem = item
                            }
                        )
                    }
                }
            }
        }
    }

    // Popup instead of a separate details screen
    selectedItem?.let { item ->

        AlertDialog(
            onDismissRequest = {
                selectedItem = null
            },

            title = {
                Text(
                    text = item.title.ifBlank {
                        "Found item"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },

            text = {

                Column(
                    modifier = Modifier
                        .heightIn(max = 350.dp)
                        .verticalScroll(
                            rememberScrollState()
                        ),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    // Image placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .background(
                                Color(0xFFEAEAEA),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            Icons.Default.Image,
                            contentDescription = "Item image",
                            modifier = Modifier.size(42.dp),
                            tint = Color.Gray
                        )
                    }

                    // Category
                    Text(
                        text = "Category",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )

                    Text(
                        text = item.category.ifBlank {
                            "Not specified"
                        },
                        fontSize = 14.sp,
                        color = CampusGray
                    )

                    // Location
                    Text(
                        text = "Found at",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )

                    Text(
                        text = item.locationName.ifBlank {
                            "Unknown location"
                        },
                        fontSize = 14.sp,
                        color = CampusGray
                    )

                    // Date
                    val dateLabel =
                        item.createdAtMillis?.let {
                            DateUtils.getRelativeTimeSpanString(
                                it,
                                System.currentTimeMillis(),
                                DateUtils.DAY_IN_MILLIS
                            ).toString()
                        } ?: "Date unavailable"

                    Text(
                        text = "Date",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )

                    Text(
                        text = dateLabel,
                        fontSize = 14.sp,
                        color = CampusGray
                    )

                    HorizontalDivider(
                        color = CampusBorder
                    )

                    // Public description
                    Text(
                        text = "Description",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )

                    Text(
                        text = item.publicDescription.ifBlank {
                            "No public description available."
                        },
                        fontSize = 14.sp,
                        color = CampusGray
                    )
                }
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        selectedItem = null
                    }
                ) {

                    Text(
                        text = "Close",
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            },

            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }
}

@Composable
private fun SearchFoundItemCard(
    item: SearchFoundItem,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                CampusBorder,
                RoundedCornerShape(10.dp)
            )
            .clickable(
                onClick = onClick
            )
            .padding(10.dp),

        horizontalArrangement =
            Arrangement.spacedBy(12.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        // Item image placeholder
        Box(
            modifier = Modifier
                .size(82.dp)
                .background(
                    Color(0xFFE5E5E5),
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                Icons.Default.Image,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement =
                Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = item.title.ifBlank {
                    "Untitled item"
                },
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color.Black
            )

            val dateLabel =
                item.createdAtMillis?.let {
                    DateUtils.getRelativeTimeSpanString(
                        it,
                        System.currentTimeMillis(),
                        DateUtils.DAY_IN_MILLIS
                    ).toString()
                } ?: "Date unavailable"

            Text(
                text = "${
                    item.locationName.ifBlank {
                        "Unknown location"
                    }
                } · $dateLabel",
                fontSize = 11.sp,
                color = CampusGray
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "View details",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Spacer(
                    Modifier.width(4.dp)
                )

                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

