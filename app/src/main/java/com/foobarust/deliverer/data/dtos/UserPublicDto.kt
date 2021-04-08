package com.foobarust.deliverer.data.dtos

import com.foobarust.deliverer.constants.Constants.USER_ID_FIELD
import com.foobarust.deliverer.constants.Constants.USER_PHOTO_URL_FIELD
import com.foobarust.deliverer.constants.Constants.USER_USERNAME_FIELD
import com.google.firebase.firestore.PropertyName

data class UserPublicDto(
    @JvmField
    @PropertyName(USER_ID_FIELD)
    val id: String? = null,

    @JvmField
    @PropertyName(USER_USERNAME_FIELD)
    val username: String? = null,

    @JvmField
    @PropertyName(USER_PHOTO_URL_FIELD)
    val photoUrl: String? = null
)