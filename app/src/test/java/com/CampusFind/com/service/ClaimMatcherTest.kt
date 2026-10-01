package com.CampusFind.com.service

import org.junit.Assert.assertEquals
import org.junit.Test

class ClaimMatcherTest {

    @Test
    fun identicalTextIsLikelyMatch() {
        val result = ClaimMatcher.compare("llavero azul con foto de gato", "llavero azul con foto de gato")

        assertEquals(1.0, result.score, 0.000001)
        assertEquals(ClaimVerdict.LIKELY_MATCH, result.verdict)
    }

    @Test
    fun matchingDescriptionIsLikelyMatch() {
        val result = ClaimMatcher.compare(
            "llavero azul con foto de gato",
            "tiene un llavero azul y una foto de un gato"
        )

        assertEquals(0.8, result.score, 0.000001)
        assertEquals(ClaimVerdict.LIKELY_MATCH, result.verdict)
    }

    @Test
    fun differentDescriptionIsNoMatch() {
        val result = ClaimMatcher.compare("llavero azul con foto de gato", "es un llavero rojo")

        assertEquals(0.2, result.score, 0.000001)
        assertEquals(ClaimVerdict.NO_MATCH, result.verdict)
    }

    @Test
    fun ignoresCaseAccentsAndPunctuation() {
        val result = ClaimMatcher.compare("¡TELÉFONO, MARRÓN!", "telefono marron")

        assertEquals(1.0, result.score, 0.000001)
        assertEquals(ClaimVerdict.LIKELY_MATCH, result.verdict)
    }

    @Test
    fun emptyRegisteredTextIsNoMatch() {
        val result = ClaimMatcher.compare("", "llavero azul")

        assertEquals(0.0, result.score, 0.000001)
        assertEquals(ClaimVerdict.NO_MATCH, result.verdict)
    }

    @Test
    fun ignoresRepeatedAndShortWords() {
        val result = ClaimMatcher.compare("azul azul gato de un", "azul azul")

        assertEquals(0.5, result.score, 0.000001)
        assertEquals(ClaimVerdict.PARTIAL_MATCH, result.verdict)
    }

    @Test
    fun appliesVerdictThresholds() {
        val registered = "uno dos tres cuatro cinco seis siete ocho nueve diez"

        assertEquals(ClaimVerdict.PARTIAL_MATCH, ClaimMatcher.compare(registered, "uno dos tres").verdict)
        assertEquals(ClaimVerdict.LIKELY_MATCH, ClaimMatcher.compare(registered, "uno dos tres cuatro cinco seis").verdict)
    }

    @Test
    fun shortRegisteredWordsAndEmptyAnswerAreNoMatch() {
        assertEquals(ClaimMatchResult(0.0, ClaimVerdict.NO_MATCH), ClaimMatcher.compare("de un !", "de un"))
        assertEquals(ClaimMatchResult(0.0, ClaimVerdict.NO_MATCH), ClaimMatcher.compare("llavero", ""))
    }
}
