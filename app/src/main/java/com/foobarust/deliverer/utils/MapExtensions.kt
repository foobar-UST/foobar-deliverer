package com.foobarust.deliverer.utils

import com.foobarust.deliverer.data.models.GeolocationPoint
import com.google.android.gms.maps.model.LatLng

/**
 * Created by kevin on 3/8/21
 */

fun LatLng.asGeolocationPoint(): GeolocationPoint {
    return GeolocationPoint(
        latitude = latitude,
        longitude = longitude
    )
}