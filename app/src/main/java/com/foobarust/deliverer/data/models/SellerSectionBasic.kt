package com.foobarust.deliverer.data.models

import com.foobarust.deliverer.utils.format
import com.foobarust.deliverer.utils.getTimeBy12Hour
import com.foobarust.deliverer.utils.isSameDay
import java.util.*

/**
 * Created by kevin on 12/20/20
 */

data class SellerSectionBasic(
    val id: String,
    val title: String,
    val titleZh: String?,
    val sellerId: String,
    val sellerName: String,
    val sellerNameZh: String?,
    val deliveryTime: Date,
    val cutoffTime: Date,
    val maxUsers: Int,
    val joinedUsersCount: Int,
    val imageUrl: String?,
    val state: SellerSectionState,
    val available: Boolean
)

fun SellerSectionBasic.isRecentSection(): Boolean {
    return available &&
        state == SellerSectionState.AVAILABLE &&
        deliveryTime.isSameDay(Date())
}

fun SellerSectionBasic.getSellerNormalizedName(): String {
    return if (sellerNameZh != null) "$sellerName $sellerNameZh" else sellerName
}

fun SellerSectionBasic.getNormalizedTitle(): String {
    val title = if (titleZh != null) "$title $titleZh" else title
    val deliverTimeString = deliveryTime.getTimeBy12Hour()
    return "$title\n@ $deliverTimeString"
}

fun SellerSectionBasic.getDeliveryDateString(): String = deliveryTime.format("yyyy-MM-dd")

fun SellerSectionBasic.getCutoffTimeString(): String = cutoffTime.getTimeBy12Hour()

fun SellerSectionBasic.getDeliveryTimeString(): String = cutoffTime.getTimeBy12Hour()