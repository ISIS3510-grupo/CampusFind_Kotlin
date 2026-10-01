package com.CampusFind.com.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class ReportTimeCalculatorTest {

    @Test
    fun averagesCompletedElectronicsReports() {
        val day = 86_400_000L
        val reportedAt = 10 * day
        val reports = listOf(
            ReportTime("electronics", reportedAt, reportedAt + 4 * day),
            ReportTime("electronics", reportedAt, reportedAt + 8 * day),
            ReportTime("electronics", reportedAt, reportedAt + 5 * day),
            ReportTime("electronics", reportedAt, null),
            ReportTime("electronics", reportedAt, null)
        )

        val result = ReportTimeCalculator.calculate(reports)

        assertEquals(1, result.size)
        assertEquals("electronics", result[0].category)
        assertEquals(3, result[0].count)
        assertEquals(17.0 / 3.0, result[0].averageDays, 0.000001)
        assertEquals("5.67", String.format(Locale.US, "%.2f", result[0].averageDays))
    }

    @Test
    fun returnsEmptyResultForEmptyList() {
        assertTrue(ReportTimeCalculator.calculate(emptyList()).isEmpty())
    }

    @Test
    fun separatesCategoriesAndPreservesPartialDays() {
        val day = 86_400_000L
        val reports = listOf(
            ReportTime("electronics", day, 2 * day),
            ReportTime("clothing", day, day + day / 2),
            ReportTime("books", day, null)
        )

        val result = ReportTimeCalculator.calculate(reports)

        assertEquals(2, result.size)
        assertEquals(CategoryReportTime("electronics", 1, 1.0), result[0])
        assertEquals(CategoryReportTime("clothing", 1, 0.5), result[1])
    }
}
