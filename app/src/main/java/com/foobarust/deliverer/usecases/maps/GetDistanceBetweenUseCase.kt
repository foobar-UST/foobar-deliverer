package com.foobarust.deliverer.usecases.maps

import com.foobarust.deliverer.data.models.GeolocationPoint
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import javax.inject.Inject

/**
 * Created by kevin on 4/7/21
 */

class GetDistanceBetweenUseCase @Inject constructor() {

    operator fun invoke(
        currentLocation: GeolocationPoint,
        destination: GeolocationPoint
    ): Double = SphericalUtil.computeDistanceBetween(
        LatLng(currentLocation.latitude, currentLocation.longitude),
        LatLng(destination.latitude, destination.longitude)
    )
}