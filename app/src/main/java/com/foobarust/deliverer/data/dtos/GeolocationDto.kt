package com.foobarust.deliverer.data.dtos

import com.foobarust.deliverer.constants.Constants.GEO_LOCATION_ADDRESS_FIELD
import com.foobarust.deliverer.constants.Constants.GEO_LOCATION_ADDRESS_ZH_FIELD
import com.foobarust.deliverer.constants.Constants.GEO_LOCATION_GEOPOINT_FIELD
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName

data class GeolocationDto(
    @JvmField
    @PropertyName(GEO_LOCATION_ADDRESS_FIELD)
    val address: String? = null,

    @JvmField
    @PropertyName(GEO_LOCATION_ADDRESS_ZH_FIELD)
    val addressZh: String? = null,

    @JvmField
    @PropertyName(GEO_LOCATION_GEOPOINT_FIELD)
    val geoPoint: GeoPoint? = null
)