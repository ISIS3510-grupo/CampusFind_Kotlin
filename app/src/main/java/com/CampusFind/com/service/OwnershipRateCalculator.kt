package com.CampusFind.com.service

data class ClaimOutcome(
    val hasPrivateCharacteristics: Boolean,
    val verdict: ClaimVerdict
)

data class GroupRate(
    val total: Int,
    val successful: Int,
    val ratePercent: Double
)

data class OwnershipRates(
    val withPrivate: GroupRate,
    val withoutPrivate: GroupRate
)

object OwnershipRateCalculator {

    fun calculate(claims: List<ClaimOutcome>): OwnershipRates {
        return OwnershipRates(
            withPrivate = groupRate(claims.filter { it.hasPrivateCharacteristics }),
            withoutPrivate = groupRate(claims.filter { !it.hasPrivateCharacteristics })
        )
    }

    private fun groupRate(claims: List<ClaimOutcome>): GroupRate {
        val successful = claims.count { it.verdict == ClaimVerdict.LIKELY_MATCH }
        val ratePercent = if (claims.isEmpty()) 0.0 else successful * 100.0 / claims.size
        return GroupRate(claims.size, successful, ratePercent)
    }
}
