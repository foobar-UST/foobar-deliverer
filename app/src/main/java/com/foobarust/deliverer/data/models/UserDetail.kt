package com.foobarust.deliverer.data.models

import java.util.*

/**
 * Created by kevin on 9/12/20
 */

data class UserDetail(
    val id: String,
    val username: String,
    val email: String,
    val name: String? = null,
    val phoneNum: String? = null,
    val photoUrl: String? = null,
    val roles: List<UserRole>,
    val updatedAt: Date? = null,
    val createdRest: Boolean? = null,
    val employedBy: String? = null
)

fun UserDetail.isDataCompleted(): Boolean {
    return !name.isNullOrEmpty() && !phoneNum.isNullOrEmpty()
}

fun UserDetail.isDeliveryVerified(): Boolean {
    return roles.contains(UserRole.DELIVERER) && employedBy != null
}
