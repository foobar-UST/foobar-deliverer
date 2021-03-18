package com.foobarust.deliverer.data.response

import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 3/8/21
 */

data class DirectionsResponse(
    @SerializedName("overview_polyline")
    val encodedPoints: String
)