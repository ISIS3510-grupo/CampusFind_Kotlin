package com.CampusFind.com.ui.screens

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.CampusFind.com.service.BiometricAuthService
import com.CampusFind.com.service.BiometricAvailability
import com.CampusFind.com.ui.components.CampusBorder
import com.CampusFind.com.ui.components.CampusGray
import com.CampusFind.com.ui.components.ScreenTitle
import com.CampusFind.com.ui.components.SecondaryButton
import com.CampusFind.com.ui.components.YellowBadge
import com.CampusFind.com.viewmodel.BiometricVerificationViewModel
import com.CampusFind.com.viewmodel.ClaimSubmissionViewModel
import kotlinx.coroutines.delay

@Composable
fun BiometricVerificationScreen(
    matchId: String,
    onClaimSubmitted: () -> Unit,
    onBack: () -> Unit = {},
    biometricViewModel:
    BiometricVerificationViewModel =
        viewModel(),
    claimSubmissionViewModel: ClaimSubmissionViewModel = viewModel()
) {

    LaunchedEffect(claimSubmissionViewModel.isSubmitted) {
        if (claimSubmissionViewModel.isSubmitted) {
            delay(1000)
            onClaimSubmitted()
        }
    }

    val context =
        LocalContext.current

    val activity =
        remember(context) {
            context.findFragmentActivity()
        }

    val biometricService =
        remember(activity) {

            activity?.let {
                BiometricAuthService(it)
            }
        }

    LaunchedEffect(
        biometricService
    ) {

        if (biometricService != null) {

            biometricViewModel
                .checkAvailability(
                    biometricService
                )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {

        ScreenTitle(
            title =
                "Verify your identity",
            subtitle =
                "Biometric verification is required before continuing with a claim.",
            onBack = onBack
        )

        Spacer(
            modifier =
                Modifier.height(28.dp)
        )

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
                .padding(24.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector =
                    Icons.Default.Fingerprint,
                contentDescription =
                    "Biometric verification",
                modifier =
                    Modifier.size(68.dp)
            )

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )

            Text(
                text =
                    "Fingerprint or face recognition",
                fontSize = 20.sp,
                fontWeight =
                    FontWeight.Medium
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Text(
                text =
                    "CampusFind uses your device biometrics to confirm that you are the authenticated user before continuing.",
                fontSize = 14.sp,
                color = CampusGray
            )
        }

        Spacer(
            modifier =
                Modifier.height(28.dp)
        )

        if (
            biometricViewModel
                .isVerified
        ) {

            YellowBadge(
                text =
                    "Identity verified"
            )

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            Text(
                text =
                    "Biometric verification completed. The claim flow is now unlocked.",
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = claimSubmissionViewModel.ownershipAnswer,
                onValueChange = claimSubmissionViewModel::updateOwnershipAnswer,
                label = {
                    Text("Describe private details of the item (marks, contents, serial number)")
                },
                singleLine = false,
                minLines = 3,
                enabled = !claimSubmissionViewModel.isSending && !claimSubmissionViewModel.isSubmitted,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (biometricViewModel.isVerified) {
                        claimSubmissionViewModel.submitClaim(matchId)
                    }
                },
                enabled = claimSubmissionViewModel.ownershipAnswer.isNotBlank() &&
                    !claimSubmissionViewModel.isSending && !claimSubmissionViewModel.isSubmitted,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (claimSubmissionViewModel.isSending) "Submitting..." else "Submit claim")
            }

            claimSubmissionViewModel.errorMessage?.let { error ->
                Text(error, color = MaterialTheme.colorScheme.error)
            }

            if (claimSubmissionViewModel.isSubmitted) {
                Text("Claim submitted successfully.", fontWeight = FontWeight.Medium)
            }

        } else {

            if (
                biometricViewModel
                    .availability ==
                BiometricAvailability.AVAILABLE
            ) {

                Button(
                    enabled =
                        !biometricViewModel
                            .isAuthenticating,

                    onClick = {

                        biometricService
                            ?.let {

                                biometricViewModel
                                    .verify(it)
                            }
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(52.dp),

                    shape =
                        RoundedCornerShape(
                            10.dp
                        )
                ) {

                    if (
                        biometricViewModel
                            .isAuthenticating
                    ) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(
                                    22.dp
                                ),
                            strokeWidth =
                                2.dp,
                            color =
                                Color.White
                        )

                    } else {

                        Text(
                            text =
                                "Verify with biometrics",
                            fontSize = 18.sp,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (
            biometricViewModel
                .message != null
        ) {

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )

            Text(
                text =
                    biometricViewModel
                        .message
                        ?: "",
                fontSize = 14.sp
            )
        }

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        SecondaryButton(
            text = "Back",
            onClick = onBack
        )
    }
}

private fun Context.findFragmentActivity():
        FragmentActivity? {

    return when (this) {

        is FragmentActivity ->
            this

        is ContextWrapper ->
            baseContext
                .findFragmentActivity()

        else ->
            null
    }
}