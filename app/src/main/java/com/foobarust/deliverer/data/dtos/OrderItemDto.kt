package com.foobarust.deliverer.data.dtos

import com.foobarust.deliverer.constants.Constants.ORDER_ITEM_AMOUNTS_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_ITEM_ID_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_ITEM_ITEM_ID_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_ITEM_ITEM_IMAGE_URL_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_ITEM_ITEM_PRICE_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_ITEM_ITEM_SELLER_ID_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_ITEM_ITEM_TITLE_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_ITEM_ITEM_TITLE_ZH_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_ITEM_TOTAL_PRICE_FIELD
import com.google.firebase.firestore.PropertyName

data class OrderItemDto(
    @JvmField
    @PropertyName(ORDER_ITEM_ID_FIELD)
    val id: String? = null,

    @JvmField
    @PropertyName(ORDER_ITEM_ITEM_ID_FIELD)
    val itemId: String? = null,

    @JvmField
    @PropertyName(ORDER_ITEM_ITEM_SELLER_ID_FIELD)
    val itemSellerId: String? = null,

    @JvmField
    @PropertyName(ORDER_ITEM_ITEM_TITLE_FIELD)
    val itemTitle: String? = null,

    @JvmField
    @PropertyName(ORDER_ITEM_ITEM_TITLE_ZH_FIELD)
    val itemTitleZh: String? = null,

    @JvmField
    @PropertyName(ORDER_ITEM_ITEM_PRICE_FIELD)
    val itemPrice: Double? = null,

    @JvmField
    @PropertyName(ORDER_ITEM_ITEM_IMAGE_URL_FIELD)
    val itemImageUrl: String? = null,

    @JvmField
    @PropertyName(ORDER_ITEM_AMOUNTS_FIELD)
    val amounts: Int? = null,

    @JvmField
    @PropertyName(ORDER_ITEM_TOTAL_PRICE_FIELD)
    val totalPrice: Double? = null
)