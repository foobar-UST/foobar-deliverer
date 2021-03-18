package com.foobarust.deliverer.data.request

import com.foobarust.deliverer.constants.Constants.UPDATE_ORDER_LOCATION_REQUEST_LATITUDE
import com.foobarust.deliverer.constants.Constants.UPDATE_ORDER_LOCATION_REQUEST_LONGITUDE
import com.foobarust.deliverer.constants.Constants.UPDATE_ORDER_LOCATION_REQUEST_ORDER_ID
import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 3/10/21
 */

data class UpdateOrderLocationRequest(
    @SerializedName(UPDATE_ORDER_LOCATION_REQUEST_ORDER_ID)
    val orderId: String,

    @SerializedName(UPDATE_ORDER_LOCATION_REQUEST_LATITUDE)
    val latitude: Double,

    @SerializedName(UPDATE_ORDER_LOCATION_REQUEST_LONGITUDE)
    val longitude: Double
)