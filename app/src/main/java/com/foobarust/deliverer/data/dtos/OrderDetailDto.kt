package com.foobarust.deliverer.data.dtos

import com.foobarust.deliverer.constants.Constants.ORDER_CREATED_AT_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_DELIVERER_ID_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_DELIVERER_LOCATION_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_DELIVERY_COST_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_DELIVERY_LOCATION_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_IDENTIFIER_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_ID_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_IMAGE_URL_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_IS_PAID_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_MESSAGE_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_ORDER_ITEMS_COUNT_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_ORDER_ITEMS_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_PAYMENT_METHOD_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_SECTION_ID_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_SECTION_TITLE_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_SECTION_TITLE_ZH_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_SELLER_ID_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_SELLER_NAME_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_SELLER_NAME_ZH_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_STATE_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_SUBTOTAL_COST_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_TITLE_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_TITLE_ZH_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_TOTAL_COST_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_TYPE_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_UPDATED_AT_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_USER_ID_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_VERIFY_CODE_FIELD
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName

data class OrderDetailDto(
    @JvmField
    @PropertyName(ORDER_ID_FIELD)
    val id: String? = null,

    @JvmField
    @PropertyName(ORDER_TITLE_FIELD)
    val title: String? = null,

    @JvmField
    @PropertyName(ORDER_TITLE_ZH_FIELD)
    val titleZh: String? = null,

    @JvmField
    @PropertyName(ORDER_USER_ID_FIELD)
    val userId: String? = null,

    @JvmField
    @PropertyName(ORDER_SELLER_ID_FIELD)
    val sellerId: String? = null,

    @JvmField
    @PropertyName(ORDER_SELLER_NAME_FIELD)
    val sellerName: String? = null,

    @JvmField
    @PropertyName(ORDER_SELLER_NAME_ZH_FIELD)
    val sellerNameZh: String? = null,

    @JvmField
    @PropertyName(ORDER_SECTION_ID_FIELD)
    val sectionId: String? = null,

    @JvmField
    @PropertyName(ORDER_SECTION_TITLE_FIELD)
    val sectionTitle: String? = null,

    @JvmField
    @PropertyName(ORDER_SECTION_TITLE_ZH_FIELD)
    val sectionTitleZh: String? = null,

    @JvmField
    @PropertyName(ORDER_DELIVERER_ID_FIELD)
    val delivererId: String? = null,

    @JvmField
    @PropertyName(ORDER_DELIVERER_LOCATION_FIELD)
    val delivererLocation: GeoPoint? = null,

    @JvmField
    @PropertyName(ORDER_IDENTIFIER_FIELD)
    val identifier: String? = null,

    @JvmField
    @PropertyName(ORDER_IMAGE_URL_FIELD)
    val imageUrl: String? = null,

    @JvmField
    @PropertyName(ORDER_TYPE_FIELD)
    val type: Int? = null,

    @JvmField
    @PropertyName(ORDER_ORDER_ITEMS_FIELD)
    val orderItems: List<OrderItemDto>? = null,

    @JvmField
    @PropertyName(ORDER_ORDER_ITEMS_COUNT_FIELD)
    val orderItemsCount: Int? = null,

    @JvmField
    @PropertyName(ORDER_STATE_FIELD)
    val state: String? = null,

    @JvmField
    @PropertyName(ORDER_IS_PAID_FIELD)
    val isPaid: Boolean? = null,

    @JvmField
    @PropertyName(ORDER_PAYMENT_METHOD_FIELD)
    val paymentMethod: String? = null,

    @JvmField
    @PropertyName(ORDER_MESSAGE_FIELD)
    val message: String? = null,

    @JvmField
    @PropertyName(ORDER_DELIVERY_LOCATION_FIELD)
    val deliveryLocation: GeolocationDto? = null,

    @JvmField
    @PropertyName(ORDER_SUBTOTAL_COST_FIELD)
    val subtotalCost: Double? = null,

    @JvmField
    @PropertyName(ORDER_DELIVERY_COST_FIELD)
    val deliveryCost: Double? = null,

    @JvmField
    @PropertyName(ORDER_TOTAL_COST_FIELD)
    val totalCost: Double? = null,

    @JvmField
    @PropertyName(ORDER_VERIFY_CODE_FIELD)
    val verifyCode: String? = null,

    @JvmField
    @PropertyName(ORDER_CREATED_AT_FIELD)
    val createdAt: Timestamp? = null,

    @JvmField
    @PropertyName(ORDER_UPDATED_AT_FIELD)
    val updatedAt: Timestamp? = null
)