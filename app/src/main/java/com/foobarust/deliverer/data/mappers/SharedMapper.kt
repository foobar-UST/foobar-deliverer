package com.foobarust.deliverer.data.mappers

import com.foobarust.deliverer.data.dtos.GeolocationDto
import com.foobarust.deliverer.data.models.Geolocation
import com.foobarust.deliverer.data.models.GeolocationPoint

/**
 * Created by kevin on 3/1/21
 */

internal fun GeolocationDto.toGeolocation(): Geolocation {
    return Geolocation(
        address = address!!,
        addressZh = addressZh,
        locationPoint = GeolocationPoint(
            latitude = geoPoint?.latitude ?: 0.toDouble(),
            longitude = geoPoint?.longitude ?: 0.toDouble()
        )
    )
}
