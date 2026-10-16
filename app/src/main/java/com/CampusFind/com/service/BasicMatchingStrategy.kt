package com.CampusFind.com.service

import com.CampusFind.com.model.FoundItemCandidate
import com.CampusFind.com.model.LostReport
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class BasicMatchingStrategy : MatchingStrategy {

    override fun calculateScore(
        lost: LostReport,
        found: FoundItemCandidate
    ): Double {

        val categoryScore =
            if (
                normalize(lost.category) ==
                normalize(found.category)
            ) {
                1.0
            } else {
                0.0
            }

        val locationScore =
            locationScore(
                lost,
                found
            )

        val dateScore =
            dateScore(
                lost,
                found
            )

        val textScore =
            textScore(
                "${lost.title} ${lost.description}",
                "${found.title} ${found.publicDescription}"
            )

        return (
                categoryScore * 0.40 +
                        locationScore * 0.25 +
                        dateScore * 0.20 +
                        textScore * 0.15
                ).coerceIn(
                0.0,
                1.0
            )
    }

    private fun normalize(
        value: String
    ): String {

        return value
            .lowercase()
            .trim()
    }

    private fun locationScore(
        lost: LostReport,
        found: FoundItemCandidate
    ): Double {

        val lostLocation =
            normalize(
                lost.locationName
            )

        val foundLocation =
            normalize(
                found.locationName
            )

        if (
            lostLocation.isNotEmpty() &&
            lostLocation == foundLocation
        ) {
            return 1.0
        }

        if (
            !validCoordinates(
                lost.latitude,
                lost.longitude
            ) ||
            !validCoordinates(
                found.latitude,
                found.longitude
            )
        ) {
            return 0.0
        }

        val distance =
            distanceInMeters(
                lost.latitude!!,
                lost.longitude!!,
                found.latitude!!,
                found.longitude!!
            )

        return when {

            distance <= 100 ->
                1.0

            distance <= 300 ->
                0.8

            distance <= 700 ->
                0.5

            distance <= 1500 ->
                0.2

            else ->
                0.0
        }
    }

    private fun validCoordinates(
        latitude: Double?,
        longitude: Double?
    ): Boolean {

        return latitude != null &&
                longitude != null &&
                latitude.isFinite() &&
                longitude.isFinite() &&
                latitude in -90.0..90.0 &&
                longitude in -180.0..180.0
    }

    private fun distanceInMeters(
        latitude1: Double,
        longitude1: Double,
        latitude2: Double,
        longitude2: Double
    ): Double {

        val earthRadius =
            6371000.0

        val latitudeDifference =
            Math.toRadians(
                latitude2 - latitude1
            )

        val longitudeDifference =
            Math.toRadians(
                longitude2 - longitude1
            )

        val a =
            sin(
                latitudeDifference / 2
            ).pow(2) +
                    cos(
                        Math.toRadians(latitude1)
                    ) *
                    cos(
                        Math.toRadians(latitude2)
                    ) *
                    sin(
                        longitudeDifference / 2
                    ).pow(2)

        val boundedA =
            a.coerceIn(
                0.0,
                1.0
            )

        return earthRadius *
                2 *
                atan2(
                    sqrt(boundedA),
                    sqrt(1 - boundedA)
                )
    }

    private fun dateScore(
        lost: LostReport,
        found: FoundItemCandidate
    ): Double {

        val reportedAt =
            lost.reportedAt
                ?: return 0.0

        val createdAt =
            found.createdAt
                ?: return 0.0

        val difference =
            abs(
                reportedAt.toDate().time -
                        createdAt.toDate().time
            )

        val days =
            TimeUnit.MILLISECONDS
                .toDays(
                    difference
                )

        return when {

            days <= 1 ->
                1.0

            days <= 3 ->
                0.75

            days <= 7 ->
                0.40

            else ->
                0.0
        }
    }

    private fun textScore(
        lostText: String,
        foundText: String
    ): Double {

        val lostWords =
            words(
                lostText
            )

        val foundWords =
            words(
                foundText
            )

        val union =
            lostWords union
                    foundWords

        if (union.isEmpty()) {
            return 0.0
        }

        val intersection =
            lostWords intersect
                    foundWords

        return intersection.size
            .toDouble() /
                union.size
                    .toDouble()
    }

    private fun words(
        text: String
    ): Set<String> {

        return text
            .lowercase()
            .replace(
                Regex(
                    "[^\\p{L}\\p{N}\\s]"
                ),
                " "
            )
            .split(
                Regex("\\s+")
            )
            .filter {
                it.length >= 3
            }
            .toSet()
    }
}