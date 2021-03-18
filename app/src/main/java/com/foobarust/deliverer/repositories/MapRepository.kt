package com.foobarust.deliverer.repositories

import com.foobarust.deliverer.data.models.GeolocationPoint

/**
 * Created by kevin on 3/8/21
 */

interface MapRepository {

    suspend fun getDirectionsPath(
        originLatitude: Double, originLongitude: Double,
        destLatitude: Double, destLongitude: Double
    ): List<GeolocationPoint>
}