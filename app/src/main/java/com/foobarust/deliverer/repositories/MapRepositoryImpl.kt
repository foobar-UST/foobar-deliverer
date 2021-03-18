package com.foobarust.deliverer.repositories

import com.foobarust.deliverer.BuildConfig.GOOGLE_MAPS_API_KEY
import com.foobarust.deliverer.api.MapService
import com.foobarust.deliverer.data.models.GeolocationPoint
import com.foobarust.deliverer.utils.asGeolocationPoint
import com.google.maps.android.PolyUtil
import javax.inject.Inject

/**
 * Created by kevin on 3/8/21
 */

class MapRepositoryImpl @Inject constructor(
    private val mapService: MapService
) : MapRepository {

    override suspend fun getDirectionsPath(
        originLatitude: Double,
        originLongitude: Double,
        destLatitude: Double,
        destLongitude: Double
    ): List<GeolocationPoint> {
        val response = mapService.getDirections(
            key = GOOGLE_MAPS_API_KEY,
            origin = "$originLatitude,$originLongitude",
            destination = "$destLatitude,$destLongitude"
        )

        return PolyUtil.decode(response.encodedPoints)
            .map { it.asGeolocationPoint() }
    }
}