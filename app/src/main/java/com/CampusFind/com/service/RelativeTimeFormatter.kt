package com.CampusFind.com.service

import java.util.concurrent.TimeUnit

object RelativeTimeFormatter {

    fun format(createdAtMillis: Long, nowMillis: Long): String {
        val elapsedMillis = (nowMillis - createdAtMillis).coerceAtLeast(0)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(elapsedMillis)
        val days = TimeUnit.MILLISECONDS.toDays(elapsedMillis)

        return when {
            days == 1L -> "1 day ago"
            days > 1L -> "$days days ago"
            hours > 0L -> "$hours h ago"
            minutes > 0L -> "$minutes min ago"
            else -> "Just now"
        }
    }
}
