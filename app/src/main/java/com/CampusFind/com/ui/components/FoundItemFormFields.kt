package com.CampusFind.com.ui.components

import android.content.ActivityNotFoundException
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import java.io.File

@Composable
private fun FoundItemTextField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    FormLabel(text = label)
    CampusTextField(value = value, onValueChange = onValueChange, placeholder = placeholder)
    Spacer(Modifier.height(14.dp))
}

@Composable
private fun FoundItemDropdownField(
    label: String,
    value: String,
    options: List<String>,
    placeholder: String,
    onSelected: (String) -> Unit
) {
    FormLabel(text = label)
    CampusDropdownField(
        value = value,
        options = options,
        onSelected = onSelected,
        placeholder = placeholder
    )
    Spacer(Modifier.height(14.dp))
}

@Composable
internal fun FoundItemDetailsFields(
    category: String,
    categories: List<String>,
    onCategoryChange: (String) -> Unit,
    description: String,
    descriptionLabel: String,
    onDescriptionChange: (String) -> Unit,
    location: String,
    locations: List<String>,
    locationLabel: String,
    onLocationChange: (String) -> Unit,
    time: String,
    timeLabel: String,
    onTimeChange: (String) -> Unit
) {
    FoundItemDropdownField("Category", category, categories, "Select category", onCategoryChange)
    FoundItemTextField(descriptionLabel, description, "Black USB-C charger", onDescriptionChange)
    FoundItemDropdownField(locationLabel, location, locations, "Select location", onLocationChange)
    FoundItemTextField(timeLabel, time, "Today · 9:10 AM", onTimeChange)
}

@Composable
internal fun FoundItemPhotoField(
    photoUri: Uri?,
    onPhotoTaken: (Uri?) -> Unit,
    logTag: String,
    enabled: Boolean = true
) {
    val context = LocalContext.current

    var pendingPhotoUri by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            onPhotoTaken(pendingPhotoUri)
        }
        pendingPhotoUri = null
    }

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
            )
            .clip(RoundedCornerShape(10.dp))
            .clickable(enabled = enabled) {
                val photoDirectory = File(context.cacheDir, "photos")
                photoDirectory.mkdirs()
                val photoFile = File.createTempFile("found_item_", ".jpg", photoDirectory)
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    photoFile
                )
                pendingPhotoUri = uri
                try {
                    cameraLauncher.launch(uri)
                } catch (exception: ActivityNotFoundException) {
                    pendingPhotoUri = null

                    if (!photoFile.delete()) {
                        Log.w(
                            logTag,
                            "Unable to delete temporary photo."
                        )
                    }
                    Toast.makeText(context, "No camera app available.", Toast.LENGTH_SHORT).show()
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (photoUri != null) {
            AndroidView(
                factory = { imageContext ->
                    ImageView(imageContext).apply {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        contentDescription = "Found item photo"
                    }
                },
                update = { imageView ->
                    imageView.setImageURI(photoUri)
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
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
    }

    Spacer(
        modifier = Modifier.height(24.dp)
    )
}

@Composable
internal fun FoundItemOwnershipField(
    value: String,
    onValueChange: (String) -> Unit,
    notice: String
) {
    FoundItemTextField(
        label = "Private ownership detail",
        value = value,
        placeholder = "Example: serial / unique mark",
        onValueChange = onValueChange
    )
    Text(text = notice, fontSize = 13.sp, color = CampusGray)
    Spacer(Modifier.height(24.dp))
}

@Composable
internal fun FoundItemOfflineNotice(text: String) {
    Text(text = text, fontSize = 13.sp, color = CampusGray)
    Spacer(Modifier.height(8.dp))
}

@Composable
internal fun FoundItemSubmitButton(
    text: String,
    isSaving: Boolean,
    photoUri: () -> Uri?,
    submit: (Uri?, () -> Unit, () -> Unit, (String) -> Unit) -> Unit,
    onComplete: () -> Unit
) {
    val context = LocalContext.current

    PrimaryButton(
        text = if (isSaving) "Saving..." else text,
        onClick = {
            submit(
                photoUri(),
                {
                    Toast.makeText(context, "Found item saved.", Toast.LENGTH_SHORT).show()
                    onComplete()
                },
                {
                    Toast.makeText(
                        context,
                        "Saved offline. It will be sent when you are back online.",
                        Toast.LENGTH_LONG
                    ).show()
                    onComplete()
                },
                { error ->
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                }
            )
        }
    )
}
