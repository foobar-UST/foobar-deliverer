package com.foobarust.deliverer.data.response

import com.foobarust.deliverer.constants.Constants.VERIFY_DELIVERER_RESPONSE_VERIFIED
import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 3/1/21
 */


data class VerifyDelivererResponse(
    @SerializedName(VERIFY_DELIVERER_RESPONSE_VERIFIED)
    val isVerified: Boolean
)