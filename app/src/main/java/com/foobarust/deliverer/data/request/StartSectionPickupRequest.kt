package com.foobarust.deliverer.data.request

import com.foobarust.deliverer.constants.Constants.START_SECTION_PICK_UP_REQUEST_SECTION_ID
import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 4/7/21
 */

data class StartSectionPickupRequest(
    @SerializedName(START_SECTION_PICK_UP_REQUEST_SECTION_ID)
    val sectionId: String
)