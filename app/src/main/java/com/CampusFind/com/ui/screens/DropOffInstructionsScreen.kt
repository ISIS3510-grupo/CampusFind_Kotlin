package com.CampusFind.com.ui.screens

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.CampusFind.com.service.GoogleMapsAdapter
import com.CampusFind.com.ui.components.CampusBorder
import com.CampusFind.com.ui.components.ScreenTitle
import com.CampusFind.com.viewmodel.DropOffInstructionsViewModel

@Composable
fun DropOffInstructionsScreen(
    onBack: () -> Unit = {},
    onDone: () -> Unit = {},
    dropOffViewModel: DropOffInstructionsViewModel = viewModel()
) {

    val context =
        LocalContext.current

    val mapService =
        remember {
            GoogleMapsAdapter(
                context.applicationContext
            )
        }

    LaunchedEffect(Unit) {

        dropOffViewModel
            .loadOffice()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(24.dp)
    ) {

        ScreenTitle(
            title = "Drop-off instructions",
            subtitle =
                "Found item report registered successfully.",
            onBack = onBack
        )

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        if (
            dropOffViewModel
                .isLoading
        ) {

            CircularProgressIndicator()

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )
        }

        val office =
            dropOffViewModel.office

        if (office != null) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = CampusBorder,
                        shape =
                            RoundedCornerShape(
                                10.dp
                            )
                    )
                    .padding(20.dp)
            ) {

                Text(
                    text =
                        "Lost & Found Office",
                    fontSize = 20.sp,
                    fontWeight =
                        FontWeight.Medium
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text = office.name,
                    fontSize = 16.sp
                )

                if (
                    office.address
                        .isNotBlank()
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(
                                8.dp
                            )
                    )

                    Text(
                        text =
                            office.address,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            Button(
                enabled =
                    office.active &&
                            office.hasCoordinates,

                onClick = {

                    val opened =
                        mapService
                            .openDirections(
                                latitude =
                                    office.latitude!!,

                                longitude =
                                    office.longitude!!
                            )

                    if (!opened) {

                        Toast.makeText(
                            context,
                            "Unable to open Google Maps.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    text =
                        "Open in Maps"
                )
            }
        }

        if (
            dropOffViewModel
                .errorMessage != null
        ) {

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            Text(
                text =
                    dropOffViewModel
                        .errorMessage
                        ?: ""
            )
        }

        Spacer(
            modifier =
                Modifier.height(28.dp)
        )

        Text(
            text =
                "Before you leave",
            fontSize = 20.sp,
            fontWeight =
                FontWeight.Medium
        )

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        InstructionCard(
            number = 1,
            text =
                "Bring the found item to the Lost & Found office."
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        InstructionCard(
            number = 2,
            text =
                "Tell staff that the item was already reported in CampusFind."
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        InstructionCard(
            number = 3,
            text =
                "Staff will register the physical drop-off."
        )

        Spacer(
            modifier =
                Modifier.height(32.dp)
        )

        OutlinedButton(
            onClick = onDone,
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(
                text = "Done"
            )
        }
    }
}

@Composable
private fun InstructionCard(
    number: Int,
    text: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = CampusBorder,
                shape =
                    RoundedCornerShape(
                        10.dp
                    )
            )
            .padding(18.dp)
    ) {

        Text(
            text = "$number.",
            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.width(14.dp)
        )

        Text(
            text = text
        )
    }
}