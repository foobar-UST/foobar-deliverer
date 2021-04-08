package com.foobarust.deliverer.data.mappers

import com.foobarust.deliverer.data.dtos.GeolocationDto
import com.foobarust.deliverer.data.models.Geolocation
import com.foobarust.deliverer.data.models.GeolocationPoint
import com.google.firebase.firestore.GeoPoint

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

internal fun GeoPoint.toGeolocationPoint(): GeolocationPoint {
    return GeolocationPoint(latitude = latitude, longitude = longitude)
}
