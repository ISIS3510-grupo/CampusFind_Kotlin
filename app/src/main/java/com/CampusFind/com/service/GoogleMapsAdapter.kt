package com.CampusFind.com.service

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

class GoogleMapsAdapter(
    private val context: Context
) : MapService {

    override fun openDirections(
        latitude: Double,
        longitude: Double
    ): Boolean {

        val uri =
            Uri.parse(
                "https://www.google.com/maps/dir/" +
                        "?api=1&destination=$latitude,$longitude"
            )

        val intent =
            Intent(
                Intent.ACTION_VIEW,
                uri
            ).apply {
                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                )
            }

        return try {

            context.startActivity(intent)

            true

        } catch (exception: ActivityNotFoundException) {

            false

        } catch (exception: Exception) {

            false
        }
    }
}