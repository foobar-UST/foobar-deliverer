package com.foobarust.deliverer.data.mappers

import com.foobarust.deliverer.constants.Constants.ORDER_STATE_ARCHIVED
import com.foobarust.deliverer.constants.Constants.ORDER_STATE_CANCELLED
import com.foobarust.deliverer.constants.Constants.ORDER_STATE_DELIVERED
import com.foobarust.deliverer.constants.Constants.ORDER_STATE_IN_TRANSIT
import com.foobarust.deliverer.constants.Constants.ORDER_STATE_PREPARING
import com.foobarust.deliverer.constants.Constants.ORDER_STATE_PROCESSING
import com.foobarust.deliverer.constants.Constants.ORDER_STATE_READY_FOR_PICK_UP
import com.foobarust.deliverer.data.dtos.OrderBasicDto
import com.foobarust.deliverer.data.models.GeolocationPoint
import com.foobarust.deliverer.data.models.OrderBasic
import com.foobarust.deliverer.data.models.OrderState
import com.foobarust.deliverer.data.models.OrderType
import com.foobarust.deliverer.data.request.UpdateOrderLocationRequest
import javax.inject.Inject

/**
 * Created by kevin on 3/9/21
 */

class OrderMapper @Inject constructor() {

    fun toOrderBasic(dto: OrderBasicDto): OrderBasic {
        return OrderBasic(
            id = dto.id!!,
            title = dto.title!!,
            titleZh = dto.titleZh,
            sellerId = dto.sellerId!!,
            sellerName = dto.sellerName!!,
            sellerNameZh = dto.sellerNameZh,
            sectionId = dto.sectionId,
            identifier = dto.identifier!!,
            imageUrl = dto.imageUrl,
            type = OrderType.values()[dto.type!!],
            orderItemsCount = dto.orderItemsCount!!,
            state = toOrderState(dto.state!!),
            deliveryAddress = dto.deliveryAddress!!,
            deliveryAddressZh = dto.deliveryAddressZh,
            totalCost = dto.totalCost!!,
            createdAt = dto.createdAt!!.toDate(),
            updatedAt = dto.updatedAt!!.toDate()
        )
    }

    fun toUpdateOrderLocationRequest(
        orderId: String,
        geolocationPoint: GeolocationPoint
    ): UpdateOrderLocationRequest {
        return UpdateOrderLocationRequest(
            orderId = orderId,
            latitude = geolocationPoint.latitude,
            longitude = geolocationPoint.longitude
        )
    }

    private fun toOrderState(state: String): OrderState {
        return when (state) {
            ORDER_STATE_PROCESSING -> OrderState.PROCESSING
            ORDER_STATE_PREPARING -> OrderState.PREPARING
            ORDER_STATE_IN_TRANSIT -> OrderState.IN_TRANSIT
            ORDER_STATE_READY_FOR_PICK_UP -> OrderState.READY_FOR_PICK_UP
            ORDER_STATE_DELIVERED -> OrderState.DELIVERED
            ORDER_STATE_ARCHIVED -> OrderState.ARCHIVED
            ORDER_STATE_CANCELLED -> OrderState.CANCELLED
            else -> throw IllegalStateException("Unknown order state: $state")
        }
    }

    private fun fromOrderState(orderState: OrderState): String {
        return when (orderState) {
            OrderState.PROCESSING -> ORDER_STATE_PROCESSING
            OrderState.PREPARING -> ORDER_STATE_PREPARING
            OrderState.IN_TRANSIT -> ORDER_STATE_IN_TRANSIT
            OrderState.READY_FOR_PICK_UP -> ORDER_STATE_READY_FOR_PICK_UP
            OrderState.DELIVERED -> ORDER_STATE_DELIVERED
            OrderState.ARCHIVED -> ORDER_STATE_ARCHIVED
            OrderState.CANCELLED -> ORDER_STATE_CANCELLED
        }
    }
}