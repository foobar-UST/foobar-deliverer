package com.foobarust.deliverer.utils

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

/**
 * Created by kevin on 3/18/21
 */

fun GoogleMap.setCameraPosition(center: LatLng, bearing: Float? = null) {
    val currentPosition = CameraPosition.Builder()
        .target(center)
        .zoom(18f)

    if (bearing != null) {
        currentPosition.bearing(bearing)
    }

    moveCamera(CameraUpdateFactory.newCameraPosition(currentPosition.build()))
}