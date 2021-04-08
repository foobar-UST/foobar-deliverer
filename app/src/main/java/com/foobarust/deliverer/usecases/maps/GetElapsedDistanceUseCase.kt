package com.foobarust.deliverer.usecases.maps

import com.foobarust.deliverer.data.models.GeolocationPoint
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import javax.inject.Inject

/**
 * Created by kevin on 4/1/21
 */

class GetElapsedDistanceUseCase @Inject constructor() {

    private var lastLocation: GeolocationPoint? = null

    operator fun invoke(currentLocation: GeolocationPoint): Double? {
        val distance = lastLocation?.let {
            SphericalUtil.computeDistanceBetween(
                LatLng(it.latitude, it.longitude),
                LatLng(currentLocation.latitude, currentLocation.longitude)
            )
        }

        lastLocation = currentLocation

        return distance
    }
}