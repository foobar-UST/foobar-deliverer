package com.foobarust.deliverer.data.models

/**
 * Created by kevin on 1/9/21
 */

data class PaymentMethod(
    val id: String,
    val identifier: String,
    val enabled: Boolean
)