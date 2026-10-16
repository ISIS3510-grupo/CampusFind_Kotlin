package com.CampusFind.com.service

import java.text.Normalizer

/** Heurística de coincidencia por palabras, con umbrales elegidos por el equipo y sin calibración con datos reales. */
// displayName proporciona una etiqueta legible sin mostrar el nombre técnico del enum.
enum class ClaimVerdict(val displayName: String) {
    LIKELY_MATCH("Likely match"),
    PARTIAL_MATCH("Partial match"),
    NO_MATCH("No match")
}

/** Resultado heurístico: score es una proporción, no una probabilidad calibrada. */
data class ClaimMatchResult(
    val score: Double,
    val verdict: ClaimVerdict
)

object ClaimMatcher {

    /** Calcula la fracción de palabras privadas únicas presentes en la respuesta (0 a 1). Asigna Likely match desde 0.60, Partial match desde 0.30 y No match por debajo. */
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

    /** Normaliza minúsculas y tildes, elimina caracteres no alfabéticos y conserva palabras únicas de al menos tres letras. Los números no participan en la comparación. */
    private fun words(text: String): Set<String> {
        return Normalizer.normalize(text.lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "")
            .replace(Regex("[^\\p{L}\\s]"), " ")
            .split(Regex("\\s+"))
            .filter { it.length >= 3 }
            .toSet()
    }
}
