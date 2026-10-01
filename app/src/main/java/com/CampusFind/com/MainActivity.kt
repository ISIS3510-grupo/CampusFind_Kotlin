package com.CampusFind.com

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import com.CampusFind.com.navigation.AppNavigation
import com.CampusFind.com.ui.theme.CampusFindTheme

class MainActivity : FragmentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContent {

            CampusFindTheme {

                AppNavigation()
            }
        }
    }
}