package com.CampusFind.com.service

import java.util.concurrent.TimeUnit
import org.junit.Assert.assertEquals
import org.junit.Test

class RelativeTimeFormatterTest {

    private val now = TimeUnit.DAYS.toMillis(10)

    @Test
    fun formatsRecentAndFutureTimesAsJustNow() {
        assertEquals("Just now", RelativeTimeFormatter.format(now - 59_999, now))
        assertEquals("Just now", RelativeTimeFormatter.format(now + 1, now))
    }

    @Test
    fun formatsElapsedMinutesHoursAndDays() {
        assertEquals("10 min ago", RelativeTimeFormatter.format(now - TimeUnit.MINUTES.toMillis(10), now))
        assertEquals("2 h ago", RelativeTimeFormatter.format(now - TimeUnit.HOURS.toMillis(2), now))
        assertEquals("1 day ago", RelativeTimeFormatter.format(now - TimeUnit.DAYS.toMillis(1), now))
        assertEquals("3 days ago", RelativeTimeFormatter.format(now - TimeUnit.DAYS.toMillis(3), now))
    }
}
