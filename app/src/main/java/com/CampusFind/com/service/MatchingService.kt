package com.CampusFind.com.service

import com.CampusFind.com.model.FoundItemCandidate
import com.CampusFind.com.model.LostReport
import com.CampusFind.com.model.MatchResult

class MatchingService(
    private val strategy: MatchingStrategy,
    private val threshold: Double = 0.70
) {

    fun compare(
        lost: LostReport,
        found: FoundItemCandidate
    ): Double {

        return strategy.calculateScore(
            lost,
            found
        )
    }
    fun findPossibleMatches(
        lost: LostReport,
        foundItems: List<FoundItemCandidate>
    ): List<MatchResult> {

        val results =
            mutableListOf<MatchResult>()

        foundItems.forEach { found ->

            if (
                found.status != "available"
            ) {
                return@forEach
            }

            val score =
                strategy.calculateScore(
                    lost,
                    found
                )

            if (
                score >= threshold
            ) {

                results.add(
                    MatchResult(
                        foundItem = found,
                        score = score
                    )
                )
            }
        }
        return results
            .sortedByDescending {
                it.score
            }
    }
}