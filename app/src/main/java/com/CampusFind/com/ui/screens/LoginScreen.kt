package com.CampusFind.com.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.CampusFind.com.ui.components.CampusBorder
import com.CampusFind.com.ui.components.CampusGray
import com.CampusFind.com.ui.components.CampusYellow
import com.CampusFind.com.ui.components.PrimaryButton
import com.CampusFind.com.ui.components.SecondaryButton
import com.CampusFind.com.ui.theme.AnaheimFontFamily

@Composable
fun LoginScreen(
    onStudentLogin: () -> Unit = {},
    onStaffLogin: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(CampusYellow)
                .padding(
                    horizontal = 24.dp,
                    vertical = 30.dp
                )
        ) {
            Text(
                text = "uniandes",
                fontSize = 31.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = AnaheimFontFamily
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Lost & Found",
                fontSize = 39.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Recover and return campus belongings with more certainty and traceability.",
                fontSize = 16.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(142.dp)
                    .border(
                        width = 1.dp,
                        color = CampusBorder,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(18.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )

                Spacer(
                    modifier = Modifier.width(14.dp)
                )

                Column {
                    Text(
                        text = "Institutional access",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = "Sign in with your Uniandes account to report, search and verify items.",
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(48.dp)
            )

            PrimaryButton(
                text = "Enter with Uniandes",
                onClick = onStudentLogin,
                height = 54
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            SecondaryButton(
                text = "Staff access",
                onClick = onStaffLogin
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = "Access is limited to the university community.",
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                ),
                fontSize = 13.sp,
                color = CampusGray
            )
        }
    }
}