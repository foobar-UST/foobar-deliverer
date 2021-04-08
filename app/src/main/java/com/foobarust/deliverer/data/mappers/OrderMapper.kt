package com.foobarust.deliverer.data.mappers

import com.foobarust.deliverer.constants.Constants.ORDER_STATE_ARCHIVED
import com.foobarust.deliverer.constants.Constants.ORDER_STATE_CANCELLED
import com.foobarust.deliverer.constants.Constants.ORDER_STATE_DELIVERED
import com.foobarust.deliverer.constants.Constants.ORDER_STATE_IN_TRANSIT
import com.foobarust.deliverer.constants.Constants.ORDER_STATE_PREPARING
import com.foobarust.deliverer.constants.Constants.ORDER_STATE_PROCESSING
import com.foobarust.deliverer.constants.Constants.ORDER_STATE_READY_FOR_PICK_UP
import com.foobarust.deliverer.data.dtos.OrderBasicDto
import com.foobarust.deliverer.data.dtos.OrderDetailDto
import com.foobarust.deliverer.data.dtos.OrderItemDto
import com.foobarust.deliverer.data.models.*
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
            userId = dto.userId!!,
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
    
    fun toOrderDetail(dto: OrderDetailDto): OrderDetail {
        val orderItems = dto.orderItems?.map {
            fromOrderItemDtoToOrderItem(it)
        } ?: emptyList()

        val delivererLocation = dto.delivererLocation?.let {
            GeolocationPoint(
                latitude = it.latitude, longitude = it.longitude
            )
        }

        return OrderDetail(
            id = dto.id!!,
            title = dto.title!!,
            titleZh = dto.titleZh,
            userId = dto.userId!!,
            sellerId = dto.sellerId!!,
            sellerName = dto.sellerName!!,
            sellerNameZh = dto.sellerNameZh,
            sectionId = dto.sectionId,
            sectionTitle = dto.sectionTitle,
            sectionTitleZh = dto.sectionTitleZh,
            delivererId = dto.delivererId,
            delivererLocation = delivererLocation,
            identifier = dto.identifier!!,
            imageUrl = dto.imageUrl,
            type = OrderType.values()[dto.type!!],
            orderItems = orderItems,
            orderItemsCount = dto.orderItemsCount!!,
            state = toOrderState(dto.state!!),
            isPaid = dto.isPaid!!,
            paymentMethod = dto.paymentMethod!!,
            message = dto.message,
            deliveryLocation = dto.deliveryLocation!!.toGeolocation(),
            subtotalCost = dto.subtotalCost!!,
            deliveryCost = dto.deliveryCost!!,
            totalCost = dto.totalCost!!,
            verifyCode = dto.verifyCode!!,
            createdAt = dto.createdAt!!.toDate(),
            updatedAt = dto.updatedAt!!.toDate()
        )
    }

    private fun fromOrderItemDtoToOrderItem(dto: OrderItemDto): OrderItem {
        return OrderItem(
            id = dto.id!!,
            itemId = dto.itemId!!,
            itemSellerId = dto.itemSellerId!!,
            itemTitle = dto.itemTitle!!,
            itemTitleZh = dto.itemTitleZh,
            itemPrice = dto.itemPrice!!,
            itemImageUrl = dto.itemImageUrl,
            amounts = dto.amounts!!,
            totalPrice = dto.totalPrice!!
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