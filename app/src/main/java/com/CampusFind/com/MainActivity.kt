package com.CampusFind.com

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.CampusFind.com.navigation.AppNavigation
import com.CampusFind.com.service.MatchNotificationService
import com.CampusFind.com.ui.theme.CampusFindTheme

class MainActivity : ComponentActivity() {

    private var notificationMatchId by
    mutableStateOf<String?>(null)

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        notificationMatchId =
            intent.getStringExtra(
                MatchNotificationService.EXTRA_MATCH_ID
            )

        setContent {

            CampusFindTheme {

                AppNavigation(
                    notificationMatchId =
                        notificationMatchId,

                    onNotificationNavigationHandled = {
                        notificationMatchId = null
                    }
                )
            }
        }
    }

    override fun onNewIntent(
        intent: Intent
    ) {

        super.onNewIntent(
            intent
        )

        setIntent(
            intent
        )

        notificationMatchId =
            intent.getStringExtra(
                MatchNotificationService.EXTRA_MATCH_ID
            )
    }
}