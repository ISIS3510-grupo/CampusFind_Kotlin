package com.CampusFind.com

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.CampusFind.com.ui.screens.HomeScreen
import com.CampusFind.com.ui.theme.CampusFindTheme
import com.CampusFind.com.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CampusFindTheme {

                val mainViewModel: MainViewModel = viewModel()

                HomeScreen(
                    message = mainViewModel.message
                )
            }
        }
    }
}