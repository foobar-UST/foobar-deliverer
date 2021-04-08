package com.foobarust.deliverer.data.models

import java.util.*

/**
 * Created by kevin on 1/28/21
 */

data class OrderDetail(
    val id: String,
    val title: String,
    val titleZh: String?,
    val userId: String,
    val sellerId: String,
    val sellerName: String,
    val sellerNameZh: String?,
    val sectionId: String?,
    val sectionTitle: String?,
    val sectionTitleZh: String?,
    val delivererId: String?,
    val delivererLocation: GeolocationPoint?,
    val identifier: String,
    val imageUrl: String?,
    val type: OrderType,
    val orderItems: List<OrderItem>,
    val orderItemsCount: Int,
    val state: OrderState,
    val isPaid: Boolean,
    val paymentMethod: String,
    val message: String?,
    val deliveryLocation: Geolocation,
    val subtotalCost: Double,
    val deliveryCost: Double,
    val totalCost: Double,
    val verifyCode: String,
    val createdAt: Date,
    val updatedAt: Date
)

fun OrderDetail.getNormalizedTitle(): String {
    return if (titleZh != null) "$title $titleZh" else title
}