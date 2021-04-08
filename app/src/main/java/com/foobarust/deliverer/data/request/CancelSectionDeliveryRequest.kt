package com.foobarust.deliverer.data.request

import com.foobarust.deliverer.constants.Constants.CANCEL_SECTION_DELIVERY_REQUEST_SECTION_ID
import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 3/22/21
 */

data class CancelSectionDeliveryRequest(
    @SerializedName(CANCEL_SECTION_DELIVERY_REQUEST_SECTION_ID)
    val sectionId: String
)