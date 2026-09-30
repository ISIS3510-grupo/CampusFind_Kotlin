package com.CampusFind.com.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.CampusFind.com.MainActivity
import com.CampusFind.com.model.AppNotification

class MatchNotificationService(
    private val context: Context
) {

    companion object {
        private const val CHANNEL_ID =
            "smart_match_channel"

        private const val CHANNEL_NAME =
            "Smart Match Notifications"

        private const val CHANNEL_DESCRIPTION =
            "Notifications for possible lost-item matches"

        const val EXTRA_MATCH_ID =
            "extra_match_id"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {

                    description =
                        CHANNEL_DESCRIPTION
                }

            val notificationManager =
                context.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            notificationManager
                .createNotificationChannel(
                    channel
                )
        }
    }

    fun showSmartMatchNotification(
        notification: AppNotification
    ): Boolean {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        val intent =
            Intent(
                context,
                MainActivity::class.java
            ).apply {

                flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP

                putExtra(
                    EXTRA_MATCH_ID,
                    notification.matchId
                )
            }

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                notification.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val androidNotification =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )
                .setContentTitle(
                    "Possible match found"
                )
                .setContentText(
                    "We found an item that may match your lost report."
                )
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setAutoCancel(true)
                .setContentIntent(
                    pendingIntent
                )
                .build()

        val notificationManager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        notificationManager.notify(
            notification.id.hashCode(),
            androidNotification
        )

        return true
    }
}