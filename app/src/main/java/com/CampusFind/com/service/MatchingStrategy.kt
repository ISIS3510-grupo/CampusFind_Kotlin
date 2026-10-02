package com.CampusFind.com.service

import com.CampusFind.com.model.FoundItemCandidate
import com.CampusFind.com.model.LostReport

interface MatchingStrategy {

    fun calculateScore(
        lost: LostReport,
        found: FoundItemCandidate
    ): Double
}