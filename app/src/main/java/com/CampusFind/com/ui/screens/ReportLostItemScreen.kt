package com.CampusFind.com.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.CampusFind.com.ui.components.*

@Composable
fun ReportLostItemScreen(
    suggestedLocation: String = "",
    onBack: () -> Unit = {},
    onLocationClick: () -> Unit = {},
    onSubmit: () -> Unit = {}
) {
    var category by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    var location by remember {
        mutableStateOf(suggestedLocation)
    }

    var dateTime by remember {
        mutableStateOf("")
    }

    var privateDetail by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 24.dp,
                vertical = 22.dp
            )
    ) {
        ScreenTitle(
            title = "Report lost item",
            subtitle = "Create the report from your profile",
            onBack = onBack
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        FormLabel(
            text = "Category"
        )

        CampusTextField(
            value = category,
            onValueChange = {
                category = it
            },
            placeholder = "Select category"
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        FormLabel(
            text = "Short description"
        )

        CampusTextField(
            value = description,
            onValueChange = {
                description = it
            },
            placeholder = "Black scientific calculator"
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        FormLabel(
            text = "Where was it lost?"
        )

        Box(
            modifier = Modifier.clickable {
                onLocationClick()
            }
        ) {
            CampusTextField(
                value = location,
                onValueChange = {
                    location = it
                },
                placeholder = "Mario Laserna Building"
            )
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        FormLabel(
            text = "Approximate date and time"
        )

        CampusTextField(
            value = dateTime,
            onValueChange = {
                dateTime = it
            },
            placeholder = "Sep 9 · 2:30 PM"
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        FormLabel(
            text = "Photo (optional)"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .border(
                    width = 1.dp,
                    color = CampusBorder,
                    shape = RoundedCornerShape(10.dp)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = CampusGray
            )

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Text(
                text = "Add a photo"
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        FormLabel(
            text = "Private verification detail"
        )

        CampusTextField(
            value = privateDetail,
            onValueChange = {
                privateDetail = it
            },
            placeholder = "Example: scratch near the screen"
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        PrimaryButton(
            text = "Submit report",
            onClick = onSubmit
        )
    }
}