package com.CampusFind.com.service

data class BottleneckReport(
    val id: String,
    val status: String,
    val statusChangedAt: Long?
)

data class ReportBottleneckSummary(
    val counts: Map<String, Int>,
    val mostStuckStages: List<String>,
    val missingTimestampReportIds: List<String>
) {
    val hasStuckReports: Boolean
        get() = counts.values.any { it > 0 }
}

object ReportBottleneckCalculator {

    val stages = listOf(
        "reported",
        "found",
        "ready_for_pickup",
        "claimed"
    )

    private const val SEVEN_DAYS_MS =
        7L * 24L * 60L * 60L * 1000L

    fun calculate(
        reports: List<BottleneckReport>,
        nowMillis: Long = System.currentTimeMillis()
    ): ReportBottleneckSummary {

        val counts =
            stages.associateWith { 0 }
                .toMutableMap()

        val missingTimestampIds =
            mutableListOf<String>()

        for (report in reports) {

            if (report.status !in stages) {
                continue
            }

            val statusChangedAt =
                report.statusChangedAt

            if (statusChangedAt == null) {
                missingTimestampIds.add(
                    report.id
                )
                continue
            }

            val timeInCurrentState =
                nowMillis - statusChangedAt

            if (timeInCurrentState > SEVEN_DAYS_MS) {

                counts[report.status] =
                    (counts[report.status] ?: 0) + 1
            }
        }

        val maximum =
            counts.values.maxOrNull() ?: 0

        val mostStuckStages =
            if (maximum == 0) {

                emptyList()

            } else {

                stages.filter { stage ->
                    counts[stage] == maximum
                }
            }

        return ReportBottleneckSummary(
            counts = counts,
            mostStuckStages =
                mostStuckStages,
            missingTimestampReportIds =
                missingTimestampIds
        )
    }
}