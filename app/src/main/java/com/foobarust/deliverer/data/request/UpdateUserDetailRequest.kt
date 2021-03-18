package com.foobarust.deliverer.data.request

import com.foobarust.deliverer.constants.Constants.UPDATE_USER_DETAIL_REQUEST_NAME
import com.foobarust.deliverer.constants.Constants.UPDATE_USER_DETAIL_REQUEST_PHONE_NUM
import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 3/8/21
 */

data class UpdateUserDetailRequest(

    @SerializedName(UPDATE_USER_DETAIL_REQUEST_NAME)
    val name: String?,

    @SerializedName(UPDATE_USER_DETAIL_REQUEST_PHONE_NUM)
    val phoneNum: String?
)