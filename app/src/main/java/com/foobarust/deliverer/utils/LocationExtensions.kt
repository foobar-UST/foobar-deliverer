package com.foobarust.deliverer.utils

import android.location.Location
import com.foobarust.deliverer.data.models.GeolocationPoint
import com.foobarust.deliverer.ui.main.LocationInfo
import com.google.android.gms.maps.model.LatLng

/**
 * Created by kevin on 4/1/21
 */
 
fun LocationInfo.toLocation(): Location {
    return Location("").apply {
        latitude = this@toLocation.locationPoint.latitude
        longitude = this@toLocation.locationPoint.longitude
        bearing = this@toLocation.azimuth
    }
}

fun GeolocationPoint.toLatLng(): LatLng {
    return LatLng(
        this.latitude,
        this.longitude
    )
}