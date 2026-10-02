package com.CampusFind.com.service

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class OwnershipRateCalculatorTest {

    @Test
    fun calculatesRatesForBothGroups() {
        val claims = listOf(
            ClaimOutcome(true, ClaimVerdict.LIKELY_MATCH),
            ClaimOutcome(true, ClaimVerdict.LIKELY_MATCH),
            ClaimOutcome(true, ClaimVerdict.PARTIAL_MATCH),
            ClaimOutcome(false, ClaimVerdict.LIKELY_MATCH),
            ClaimOutcome(false, ClaimVerdict.PARTIAL_MATCH),
            ClaimOutcome(false, ClaimVerdict.NO_MATCH)
        )

        val result = OwnershipRateCalculator.calculate(claims)

        assertEquals(3, result.withPrivate.total)
        assertEquals(2, result.withPrivate.successful)
        assertEquals(200.0 / 3, result.withPrivate.ratePercent, 0.000001)
        assertEquals("66.7", String.format(Locale.US, "%.1f", result.withPrivate.ratePercent))
        assertEquals(3, result.withoutPrivate.total)
        assertEquals(1, result.withoutPrivate.successful)
        assertEquals(100.0 / 3, result.withoutPrivate.ratePercent, 0.000001)
        assertEquals("33.3", String.format(Locale.US, "%.1f", result.withoutPrivate.ratePercent))
    }

    @Test
    fun partialAndNoMatchAreNotSuccessful() {
        val result = OwnershipRateCalculator.calculate(
            listOf(
                ClaimOutcome(true, ClaimVerdict.PARTIAL_MATCH),
                ClaimOutcome(true, ClaimVerdict.NO_MATCH),
                ClaimOutcome(false, ClaimVerdict.PARTIAL_MATCH),
                ClaimOutcome(false, ClaimVerdict.NO_MATCH)
            )
        )

        assertEquals(GroupRate(2, 0, 0.0), result.withPrivate)
        assertEquals(GroupRate(2, 0, 0.0), result.withoutPrivate)
    }

    @Test
    fun emptyListHasZeroTotalsAndRates() {
        val result = OwnershipRateCalculator.calculate(emptyList())

        assertEquals(GroupRate(0, 0, 0.0), result.withPrivate)
        assertEquals(GroupRate(0, 0, 0.0), result.withoutPrivate)
    }

    @Test
    fun emptyGroupHasZeroRateWhenOtherGroupHasClaims() {
        val result = OwnershipRateCalculator.calculate(
            listOf(ClaimOutcome(true, ClaimVerdict.LIKELY_MATCH))
        )

        assertEquals(GroupRate(1, 1, 100.0), result.withPrivate)
        assertEquals(GroupRate(0, 0, 0.0), result.withoutPrivate)
    }
}
