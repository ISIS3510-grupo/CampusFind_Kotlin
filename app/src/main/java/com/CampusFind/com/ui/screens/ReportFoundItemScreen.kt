package com.CampusFind.com.ui.screens

import android.net.Uri
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.CampusFind.com.ui.components.*
import com.CampusFind.com.viewmodel.FoundItemFormViewModel

@Composable
fun ReportFoundItemScreen(
    suggestedLocation: String = "",
    categories: List<String> = emptyList(),
    locations: List<String> = emptyList(),
    onBack: () -> Unit = {},
    onContinue: () -> Unit = {},
    onCancel: () -> Unit = {},
    viewModel: FoundItemFormViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.sendPending()
    }

    LaunchedEffect(suggestedLocation) {
        viewModel.initLocation(suggestedLocation)
    }

    var photoUri by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
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

        FoundItemDetailsFields(
            category = viewModel.category,
            categories = categories,
            onCategoryChange = viewModel::onCategoryChange,
            description = viewModel.description,
            descriptionLabel = "Short description",
            onDescriptionChange = viewModel::onDescriptionChange,
            location = viewModel.location,
            locations = locations,
            locationLabel = "Where did you find it?",
            onLocationChange = viewModel::onLocationChange,
            time = viewModel.time,
            timeLabel = "Approximate time",
            onTimeChange = viewModel::onTimeChange
        )

        FoundItemPhotoField(
            photoUri = photoUri,
            onPhotoTaken = { photoUri = it },
            logTag = "ReportFoundItemScreen"
        )

        FoundItemOwnershipField(
            value = viewModel.privateCharacteristics,
            onValueChange = viewModel::onPrivateCharacteristicsChange,
            notice = "Do not publish private ownershipdetails."
        )

        if (!viewModel.isConnected) {
            FoundItemOfflineNotice("No internet connection")
        }

        FoundItemSubmitButton(
            text = "Continue to drop-off",
            isSaving = viewModel.isSaving,
            photoUri = { photoUri },
            submit = viewModel::save,
            onComplete = onContinue
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
