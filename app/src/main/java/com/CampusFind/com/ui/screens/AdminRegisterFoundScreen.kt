package com.CampusFind.com.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.CampusFind.com.ui.components.*
import com.CampusFind.com.viewmodel.AdminRegisterFoundViewModel

@Composable
fun AdminRegisterFoundScreen(
    categories: List<String>,
    locations: List<String>,
    onBack: () -> Unit,
    onDashboard: () -> Unit,
    onSearch: () -> Unit,
    onClaims: () -> Unit,
    onRegistered: () -> Unit,
    viewModel: AdminRegisterFoundViewModel = viewModel()
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    BackHandler {
        viewModel.flushDraft(onBack)
    }

    DisposableEffect(lifecycle, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) viewModel.flushDraft()
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    var photoUri by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 22.dp)
        ) {
            ScreenTitle(
                title = "Register found item",
                subtitle = "Create a traceable inventory record",
                onBack = { viewModel.flushDraft(onBack) }
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            FoundItemDetailsFields(
                category = viewModel.category,
                categories = categories,
                onCategoryChange = viewModel::onCategoryChange,
                description = viewModel.description,
                descriptionLabel = "Description",
                onDescriptionChange = viewModel::onDescriptionChange,
                location = viewModel.location,
                locations = locations,
                locationLabel = "Found location",
                onLocationChange = viewModel::onLocationChange,
                time = viewModel.time,
                timeLabel = "Date and time",
                onTimeChange = viewModel::onTimeChange
            )

            FoundItemPhotoField(
                photoUri = photoUri,
                onPhotoTaken = { photoUri = it },
                logTag = "AdminRegisterFoundScreen",
                enabled = !viewModel.isSaving
            )

            FoundItemOwnershipField(
                value = viewModel.privateCharacteristics,
                onValueChange = viewModel::onPrivateCharacteristicsChange,
                notice = "Do not publish private ownership details."
            )

            if (viewModel.draftRestored) {
                Text(text = "Draft restored", fontSize = 13.sp, color = CampusGray)
            }

            if (viewModel.isDraftSaved) {
                Text(text = "Draft saved on this device", fontSize = 13.sp, color = CampusGray)
            }

            if (!viewModel.isConnected) {
                FoundItemOfflineNotice("No internet connection. Your draft is kept on this device.")
            }

            FoundItemSubmitButton(
                text = "Register item",
                isSaving = viewModel.isSaving,
                photoUri = { photoUri },
                submit = viewModel::register,
                onComplete = onRegistered
            )
        }

        AdminBottomNav(
            selected = AdminNavDestination.Register,
            onDashboard = { viewModel.flushDraft(onDashboard) },
            onSearch = { viewModel.flushDraft(onSearch) },
            onRegister = {},
            onClaims = { viewModel.flushDraft(onClaims) }
        )
    }
}
