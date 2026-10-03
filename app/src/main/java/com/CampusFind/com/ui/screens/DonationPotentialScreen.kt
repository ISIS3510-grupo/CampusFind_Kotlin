package com.CampusFind.com.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.CampusFind.com.viewmodel.DonationCategoryResult

@Composable
fun DonationPotentialScreen(
    semesterId: String,
    results: List<DonationCategoryResult>,
    isLoading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Donation Potential",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Semester $semesterId",
            fontSize = 14.sp
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    )
                )
            }

            errorMessage != null -> {
                Text(
                    text = errorMessage
                )
            }

            results.isEmpty() -> {
                Text(
                    text = "No donation-eligible items found."
                )
            }

            else -> {
                results.forEach { result ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = result.category
                        )

                        Text(
                            text = result.count.toString(),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}