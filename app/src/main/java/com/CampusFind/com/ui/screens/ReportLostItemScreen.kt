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
import com.CampusFind.com.ui.components.CampusDropdownField
import com.CampusFind.com.ui.components.FormLabel
import com.CampusFind.com.ui.components.PrimaryButton
import com.CampusFind.com.ui.components.ScreenTitle
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun ReportLostItemScreen(
    suggestedLocation: String = "",
    locationError: String? = null,
    isLocationLoading: Boolean = false,
    categories: List<String> = emptyList(),
    locations: List<String> = emptyList(),
    onBack: () -> Unit = {},
    errorMessage: String? = null,
    isSaving: Boolean = false,
    onSubmit: (String, String, String, String) -> Unit =
        { _, _, _, _ -> }
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

    // Evita que el GPS sobrescriba la elección del usuario
    var locationManuallySelected by remember {
        mutableStateOf(false)
    }

    // Actualiza la sugerencia GPS si el usuario
    // todavía no ha escogido otra ubicación
    LaunchedEffect(suggestedLocation) {
        if (
            suggestedLocation.isNotBlank() &&
            !locationManuallySelected
        ) {
            location = suggestedLocation
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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

        // CATEGORY
        FormLabel(
            text = "Category"
        )

        CampusDropdownField(
            value = category,
            options = categories,
            onSelected = {
                category = it
            },
            placeholder = "Select category"
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        // DESCRIPTION
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

        // LOCATION
        FormLabel(
            text = "Where was it lost?"
        )

        CampusDropdownField(
            value = location,
            options = locations,
            onSelected = {
                location = it
                locationManuallySelected = true
            },
            placeholder = "Select location"
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

        // DATE AND TIME
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

        // PHOTO
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

        // PRIVATE VERIFICATION DETAIL
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

        // SUBMIT
        PrimaryButton(
            text = if (isSaving) "Saving..." else "Submit report",
            onClick = {
                if (!isSaving) {
                    onSubmit(
                        category,
                        description,
                        location,
                        privateDetail
                    )
                }
            }
        )

        if (errorMessage != null) {
            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
