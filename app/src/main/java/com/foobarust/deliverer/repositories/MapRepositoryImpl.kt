package com.foobarust.deliverer.repositories

import com.foobarust.deliverer.BuildConfig.GOOGLE_MAPS_API_KEY
import com.foobarust.deliverer.api.MapService
import com.foobarust.deliverer.constants.Constants.MAPS_API_URL
import com.foobarust.deliverer.constants.Constants.MAPS_STATIC_MAP_END_POINT
import com.foobarust.deliverer.constants.Constants.MAPS_STATIC_MAP_PARAM_AUTO_SCALE
import com.foobarust.deliverer.constants.Constants.MAPS_STATIC_MAP_PARAM_FORMAT
import com.foobarust.deliverer.constants.Constants.MAPS_STATIC_MAP_PARAM_KEY
import com.foobarust.deliverer.constants.Constants.MAPS_STATIC_MAP_PARAM_MARKERS
import com.foobarust.deliverer.constants.Constants.MAPS_STATIC_MAP_PARAM_SIZE
import com.foobarust.deliverer.constants.Constants.MAPS_STATIC_MAP_PARAM_VISUAL_REFRESH
import com.foobarust.deliverer.data.mappers.MapMapper
import com.foobarust.deliverer.data.models.GeolocationPoint
import com.foobarust.deliverer.data.models.TravelMode
import com.foobarust.deliverer.utils.asGeolocationPoint
import com.google.maps.android.PolyUtil
import javax.inject.Inject

/**
 * Created by kevin on 3/8/21
 */

class MapRepositoryImpl @Inject constructor(
    private val mapService: MapService,
    private val mapMapper: MapMapper
) : MapRepository {

    override suspend fun getDirectionsPath(
        currentLocation: GeolocationPoint,
        destination: GeolocationPoint,
        travelMode: TravelMode
    ): List<GeolocationPoint> {
        val response = mapService.getDirections(
            key = GOOGLE_MAPS_API_KEY,
            origin = "${currentLocation.latitude},${currentLocation.longitude}",
            destination = "${destination.latitude},${destination.longitude}",
            travelMode = mapMapper.toTravelMode(travelMode)
        )

        return PolyUtil.decode(response.encodedPoints)
            .map { it.asGeolocationPoint() }
    }

    override fun getStaticMapImageUrl(centerLocation: GeolocationPoint): String {
        return MAPS_API_URL +
            "$MAPS_STATIC_MAP_END_POINT?" +
            "$MAPS_STATIC_MAP_PARAM_KEY=$GOOGLE_MAPS_API_KEY&" +
            "$MAPS_STATIC_MAP_PARAM_AUTO_SCALE=1&" +
            "$MAPS_STATIC_MAP_PARAM_SIZE=1920x1080&" +
            "$MAPS_STATIC_MAP_PARAM_FORMAT=png&" +
            "$MAPS_STATIC_MAP_PARAM_VISUAL_REFRESH=true&" +
            "$MAPS_STATIC_MAP_PARAM_MARKERS=${centerLocation.latitude},${centerLocation.longitude}"
    }
}