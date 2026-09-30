package com.CampusFind.com.ui.screens

import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.sp
import com.CampusFind.com.ui.components.*

@Composable
fun ReportFoundItemScreen(
    suggestedLocation: String = "",
    onBack: () -> Unit = {},
    onContinue: () -> Unit = {},
    onCancel: () -> Unit = {}
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

    var time by remember {
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
            title = "I found an item",
            subtitle = "Register useful context before dropping it off",
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
            placeholder = "Black USB-C charger"
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        FormLabel(
            text = "Where did you find it?"
        )

        CampusTextField(
            value = location,
            onValueChange = {
                location = it
            },
            placeholder = "Library · 3rd floor"
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        FormLabel(
            text = "Approximate time"
        )

        CampusTextField(
            value = time,
            onValueChange = {
                time = it
            },
            placeholder = "Today · 9:10 AM"
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
                .height(86.dp)
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
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Do not publish private ownership details.",
            fontSize = 13.sp,
            color = CampusGray
        )

        Spacer(
            modifier = Modifier.weight(1f)
        )

        PrimaryButton(
            text = "Continue to drop-off",
            onClick = onContinue
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        SecondaryButton(
            text = "Cancel",
            onClick = onCancel,
            height = 40
        )
    }
}