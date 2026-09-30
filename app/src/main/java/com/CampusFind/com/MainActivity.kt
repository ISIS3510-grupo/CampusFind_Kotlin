package com.CampusFind.com

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.CampusFind.com.navigation.AppNavigation
import com.CampusFind.com.ui.theme.CampusFindTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CampusFindTheme {
                AppNavigation()
            }
        }
    }
}