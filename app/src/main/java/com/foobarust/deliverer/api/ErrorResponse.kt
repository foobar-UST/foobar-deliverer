package com.foobarust.deliverer.api

import com.foobarust.deliverer.constants.Constants.REMOTE_ERROR_RESPONSE_CODE_FIELD
import com.foobarust.deliverer.constants.Constants.REMOTE_ERROR_RESPONSE_ERROR_OBJECT
import com.foobarust.deliverer.constants.Constants.REMOTE_ERROR_RESPONSE_MESSAGE_FIELD
import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 3/1/21
 */

data class ErrorResponse(
    @SerializedName(REMOTE_ERROR_RESPONSE_ERROR_OBJECT)
    val error: ErrorResponseContent
)

data class ErrorResponseContent(
    @SerializedName(REMOTE_ERROR_RESPONSE_CODE_FIELD)
    val code: Int,

    @SerializedName(REMOTE_ERROR_RESPONSE_MESSAGE_FIELD)
    val message: String?
) {
    override fun toString(): String = "Http $code: $message"
}