package com.foobarust.deliverer.data.mappers

import com.foobarust.deliverer.constants.Constants.MAPS_DIRECTIONS_MODE_DRIVING
import com.foobarust.deliverer.constants.Constants.MAPS_DIRECTIONS_MODE_WALKING
import com.foobarust.deliverer.data.models.TravelMode
import javax.inject.Inject

/**
 * Created by kevin on 3/22/21
 */

class MapMapper @Inject constructor() {

    fun toTravelMode(travelMode: TravelMode): String {
        return when (travelMode) {
            TravelMode.DRIVING -> MAPS_DIRECTIONS_MODE_DRIVING
            TravelMode.WALKING -> MAPS_DIRECTIONS_MODE_WALKING
        }
    }
}