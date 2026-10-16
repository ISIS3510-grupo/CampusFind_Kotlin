package com.CampusFind.com.service

/** Calcula promedios por categoría sin acceder a Firebase. */
/** Tiempos en milisegundos desde epoch; foundAt nulo indica que aún no hay hallazgo. */
data class ReportTime(
    val category: String,
    val reportedAt: Long,
    val foundAt: Long?
)

/** Resumen de una categoría: cantidad de reportes completados y duración media en días. */
data class CategoryReportTime(
    val category: String,
    val count: Int,
    val averageDays: Double
)

object ReportTimeCalculator {

    /** Agrupa reportes con foundAt y promedia (foundAt - reportedAt) / 86_400_000.0 en días. count indica reportes completados; no filtra intervalos negativos. */
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
