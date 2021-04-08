package com.foobarust.deliverer.data.models

/**
 * Created by kevin on 4/3/21
 */

data class UserPublic(
    val id: String,
    val username: String,
    val photoUrl: String? = null
)