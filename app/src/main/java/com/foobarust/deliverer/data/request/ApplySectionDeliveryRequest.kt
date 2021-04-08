package com.foobarust.deliverer.data.request

import com.foobarust.deliverer.constants.Constants.APPLY_SECTION_DELIVERY_REQUEST_SECTION_ID
import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 3/22/21
 */

data class ApplySectionDeliveryRequest(
    @SerializedName(APPLY_SECTION_DELIVERY_REQUEST_SECTION_ID)
    val sectionId: String
)