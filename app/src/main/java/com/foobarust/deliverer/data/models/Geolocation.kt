package com.foobarust.deliverer.data.models

/**
 * Created by kevin on 10/11/20
 */

data class Geolocation(
    val address: String,
    val addressZh: String?,
    val locationPoint: GeolocationPoint
)

fun Geolocation.getNormalizedAddress(): String {
    return if (addressZh != null) "$address\n$addressZh" else address
}

data class GeolocationPoint(
    val latitude: Double,
    val longitude: Double
)
