package com.foobarust.deliverer.data.request

import com.foobarust.deliverer.constants.Constants.COMPLETE_SECTION_DELIVERY_REQUEST_SECTION_ID
import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 4/8/21
 */

data class CompleteSectionDeliveryRequest(
    @SerializedName(COMPLETE_SECTION_DELIVERY_REQUEST_SECTION_ID)
    val sectionId: String
)