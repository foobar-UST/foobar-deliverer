package com.foobarust.deliverer.data.request

import com.foobarust.deliverer.constants.Constants.UPDATE_SECTION_LOCATION_REQUEST_LATITUDE
import com.foobarust.deliverer.constants.Constants.UPDATE_SECTION_LOCATION_REQUEST_LONGITUDE
import com.foobarust.deliverer.constants.Constants.UPDATE_SECTION_LOCATION_REQUEST_MODE
import com.foobarust.deliverer.constants.Constants.UPDATE_SECTION_LOCATION_REQUEST_SECTION_ID
import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 3/10/21
 */

data class UpdateSectionLocationRequest(
    @SerializedName(UPDATE_SECTION_LOCATION_REQUEST_SECTION_ID)
    val sectionId: String,

    @SerializedName(UPDATE_SECTION_LOCATION_REQUEST_LATITUDE)
    val latitude: Double,

    @SerializedName(UPDATE_SECTION_LOCATION_REQUEST_LONGITUDE)
    val longitude: Double,

    @SerializedName(UPDATE_SECTION_LOCATION_REQUEST_MODE)
    val travelMode: String
)