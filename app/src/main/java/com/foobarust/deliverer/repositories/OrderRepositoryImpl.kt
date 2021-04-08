package com.foobarust.deliverer.repositories

import com.foobarust.deliverer.api.RemoteService
import com.foobarust.deliverer.constants.Constants.ORDERS_BASIC_COLLECTION
import com.foobarust.deliverer.constants.Constants.ORDERS_COLLECTION
import com.foobarust.deliverer.constants.Constants.ORDER_SECTION_ID_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_STATE_ARCHIVED
import com.foobarust.deliverer.constants.Constants.ORDER_STATE_DELIVERED
import com.foobarust.deliverer.constants.Constants.ORDER_STATE_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_STATE_IN_TRANSIT
import com.foobarust.deliverer.constants.Constants.ORDER_STATE_READY_FOR_PICK_UP
import com.foobarust.deliverer.data.mappers.OrderMapper
import com.foobarust.deliverer.data.models.OrderBasic
import com.foobarust.deliverer.data.models.OrderDetail
import com.foobarust.deliverer.data.request.ConfirmOrderDeliveredRequest
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.utils.snapshotFlow
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by kevin on 3/9/21
 */

class OrderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val remoteService: RemoteService,
    private val orderMapper: OrderMapper
) : OrderRepository {

    override fun getOrderBasicsObservable(sectionId: String): Flow<Resource<List<OrderBasic>>> {
        return firestore.collection(ORDERS_BASIC_COLLECTION)
            .whereEqualTo(ORDER_SECTION_ID_FIELD, sectionId)
            .whereIn(ORDER_STATE_FIELD, listOf(
                ORDER_STATE_IN_TRANSIT, ORDER_STATE_READY_FOR_PICK_UP,
                ORDER_STATE_DELIVERED, ORDER_STATE_ARCHIVED
            ))
            .snapshotFlow(orderMapper::toOrderBasic)
    }

    override fun getOrderDetailObservable(orderId: String): Flow<Resource<OrderDetail>> {
        return firestore.document("$ORDERS_COLLECTION/$orderId")
            .snapshotFlow(orderMapper::toOrderDetail, keepAlive = true)
    }

    override suspend fun confirmOrderDelivered(idToken: String, orderId: String) {
        remoteService.confirmOrderDelivered(
            idToken = idToken,
            confirmOrderDeliveredRequest = ConfirmOrderDeliveredRequest(orderId)
        )
    }
}