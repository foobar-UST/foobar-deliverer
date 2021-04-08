package com.foobarust.deliverer.repositories

import com.foobarust.deliverer.data.models.GeolocationPoint
import com.foobarust.deliverer.data.models.TravelMode

/**
 * Created by kevin on 3/8/21
 */

interface MapRepository {

    suspend fun getDirectionsPath(
        currentLocation: GeolocationPoint,
        destination: GeolocationPoint,
        travelMode: TravelMode
    ): List<GeolocationPoint>

    fun getStaticMapImageUrl(centerLocation: GeolocationPoint): String
}