package com.CampusFind.com.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.CampusFind.com.ui.components.CampusBorder
import com.CampusFind.com.ui.components.CampusGray
import com.CampusFind.com.ui.components.CampusTextField
import com.CampusFind.com.ui.components.FormLabel
import com.CampusFind.com.ui.components.PrimaryButton
import com.CampusFind.com.ui.components.ScreenTitle

@Composable
fun ReportLostItemScreen(
    suggestedLocation: String = "",
    locationError: String? = null,
    isLocationLoading: Boolean = false,
    onBack: () -> Unit = {},
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

    LaunchedEffect(suggestedLocation) {
        if (suggestedLocation.isNotBlank()) {
            location = suggestedLocation
        }
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

        CampusTextField(
            value = location,
            onValueChange = {
                location = it
            },
            placeholder = "Mario Laserna Building"
        )

        if (isLocationLoading) {
            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Detecting current location...",
                fontSize = 12.sp,
                color = CampusGray
            )
        }

        if (locationError != null) {
            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = locationError,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error
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