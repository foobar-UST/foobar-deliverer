package com.foobarust.deliverer.api

import com.foobarust.deliverer.constants.Constants.REMOTE_SUCCESS_RESPONSE_DATA_OBJECT
import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 3/1/21
 */

data class SuccessResponse<T>(
    @SerializedName(REMOTE_SUCCESS_RESPONSE_DATA_OBJECT)
    val data: T
)