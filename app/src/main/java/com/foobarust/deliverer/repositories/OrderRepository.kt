package com.foobarust.deliverer.repositories

import com.foobarust.deliverer.data.models.OrderBasic
import com.foobarust.deliverer.data.models.OrderDetail
import com.foobarust.deliverer.states.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Created by kevin on 3/9/21
 */

interface OrderRepository {

    fun getOrderBasicsObservable(sectionId: String): Flow<Resource<List<OrderBasic>>>

    fun getOrderDetailObservable(orderId: String): Flow<Resource<OrderDetail>>

    suspend fun confirmOrderDelivered(idToken: String, orderId: String)
}