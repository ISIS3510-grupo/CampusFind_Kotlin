package com.CampusFind.com.service

/** Compara porcentajes de Likely match entre los grupos con y sin documento privado del reporte. */
/** Grupo del reclamo y veredicto que se usará para contar coincidencias exitosas. */
data class ClaimOutcome(
    val hasPrivateCharacteristics: Boolean,
    val verdict: ClaimVerdict
)

/** Total de reclamos, cantidad de Likely match y porcentaje calculado para un grupo. */
data class GroupRate(
    val total: Int,
    val successful: Int,
    val ratePercent: Double
)

/** Indicadores comparables para los grupos con y sin información privada del reporte. */
data class OwnershipRates(
    val withPrivate: GroupRate,
    val withoutPrivate: GroupRate
)

object OwnershipRateCalculator {

    /** Separa los reclamos según hasPrivateCharacteristics y devuelve los indicadores de ambos grupos. */
    fun calculate(claims: List<ClaimOutcome>): OwnershipRates {
        return OwnershipRates(
            withPrivate = groupRate(claims.filter { it.hasPrivateCharacteristics }),
            withoutPrivate = groupRate(claims.filter { !it.hasPrivateCharacteristics })
        )
    }

    /** Cuenta LIKELY_MATCH como éxito y calcula exitosos / total * 100; un grupo vacío devuelve 0 %. No representa una aprobación humana de propiedad. */
    private fun groupRate(claims: List<ClaimOutcome>): GroupRate {
        val successful = claims.count { it.verdict == ClaimVerdict.LIKELY_MATCH }
        val ratePercent = if (claims.isEmpty()) 0.0 else successful * 100.0 / claims.size
        return GroupRate(claims.size, successful, ratePercent)
    }
}
