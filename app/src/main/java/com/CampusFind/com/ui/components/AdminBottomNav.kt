package com.CampusFind.com.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AdminNavDestination {
    Dashboard,
    Search,
    Register,
    Claims
}

@Composable
fun AdminBottomNav(
    selected: AdminNavDestination,
    onDashboard: () -> Unit,
    onSearch: () -> Unit,
    onRegister: () -> Unit,
    onClaims: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = CampusBorder
            )
            .background(Color.White)
            .navigationBarsPadding()
            .padding(
                vertical = 8.dp
            ),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AdminBottomNavItem(
            icon = Icons.Default.Home,
            label = "Dashboard",
            selected = selected == AdminNavDestination.Dashboard,
            onClick = onDashboard
        )

        AdminBottomNavItem(
            icon = Icons.Default.Search,
            label = "Search",
            selected = selected == AdminNavDestination.Search,
            onClick = onSearch
        )

        AdminBottomNavItem(
            icon = Icons.Default.Add,
            label = "Register",
            selected = selected == AdminNavDestination.Register,
            onClick = onRegister
        )

        AdminBottomNavItem(
            icon = Icons.Default.Check,
            label = "Claims",
            selected = selected == AdminNavDestination.Claims,
            onClick = onClaims
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
