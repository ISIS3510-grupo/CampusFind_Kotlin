package com.CampusFind.com.service

import java.text.Normalizer

enum class ClaimVerdict {
    LIKELY_MATCH,
    PARTIAL_MATCH,
    NO_MATCH
}

data class ClaimMatchResult(
    val score: Double,
    val verdict: ClaimVerdict
)

object ClaimMatcher {

    fun compare(registered: String, answer: String): ClaimMatchResult {
        val registeredWords = words(registered)
        val answerWords = words(answer)
        val score = if (registeredWords.isEmpty()) {
            0.0
        } else {
            registeredWords.count { it in answerWords }.toDouble() / registeredWords.size
        }

        val verdict = when {
            score >= 0.6 -> ClaimVerdict.LIKELY_MATCH
            score >= 0.3 -> ClaimVerdict.PARTIAL_MATCH
            else -> ClaimVerdict.NO_MATCH
        }

        return ClaimMatchResult(score, verdict)
    }

    private fun words(text: String): Set<String> {
        return Normalizer.normalize(text.lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "")
            .replace(Regex("[^\\p{L}\\s]"), " ")
            .split(Regex("\\s+"))
            .filter { it.length >= 3 }
            .toSet()
    }
}
