package com.CampusFind.com.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.CampusFind.com.data.NotificationsDataSource
import com.CampusFind.com.model.AppNotification
import com.google.firebase.firestore.ListenerRegistration

class NotificationsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dataSource =
        NotificationsDataSource()

    private val preferences =
        application.getSharedPreferences(
            "smart_match_notifications",
            Context.MODE_PRIVATE
        )

    var notifications by
    mutableStateOf<List<AppNotification>>(
        emptyList()
    )
        private set

    var latestNotification by
    mutableStateOf<AppNotification?>(null)
        private set

    var errorMessage by
    mutableStateOf<String?>(null)
        private set

    private var listener:
            ListenerRegistration? = null

    fun startObserving() {

        listener?.remove()

        errorMessage = null

        listener =
            dataSource.observeNotifications(

                onUpdate = { result ->

                    notifications = result

                    findNextNotification()
                },

                onError = { error ->

                    errorMessage = error
                }
            )
    }

    private fun findNextNotification() {

        latestNotification =
            notifications.firstOrNull { notification ->

                !wasAlreadyNotified(
                    notification.id
                )
            }
    }

    private fun wasAlreadyNotified(
        notificationId: String
    ): Boolean {

        return preferences.getBoolean(
            "notified_$notificationId",
            false
        )
    }

    fun markAsNotified(
        notificationId: String
    ) {

        preferences
            .edit()
            .putBoolean(
                "notified_$notificationId",
                true
            )
            .apply()

        latestNotification = null

        findNextNotification()
    }

    override fun onCleared() {

        listener?.remove()

        super.onCleared()
    }
}