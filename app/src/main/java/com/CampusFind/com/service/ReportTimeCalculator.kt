package com.CampusFind.com.service

data class ReportTime(
    val category: String,
    val reportedAt: Long,
    val foundAt: Long?
)

data class CategoryReportTime(
    val category: String,
    val count: Int,
    val averageDays: Double
)

object ReportTimeCalculator {

    fun calculate(reports: List<ReportTime>): List<CategoryReportTime> {
        val completedReports = reports.filter { it.foundAt != null }
        return completedReports.groupBy { it.category }.map { (category, items) ->
            val averageDays = items.map { report ->
                (report.foundAt!! - report.reportedAt) / 86_400_000.0
            }.average()

            CategoryReportTime(category, items.size, averageDays)
        }
    }
}
