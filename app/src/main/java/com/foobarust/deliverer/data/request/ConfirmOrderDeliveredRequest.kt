package com.foobarust.deliverer.data.request

import com.foobarust.deliverer.constants.Constants.CONFIRM_ORDER_DELIVERED_REQUEST_ORDER_ID
import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 3/22/21
 */

data class ConfirmOrderDeliveredRequest(
    @SerializedName(CONFIRM_ORDER_DELIVERED_REQUEST_ORDER_ID)
    val orderId: String
)