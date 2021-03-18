package com.foobarust.deliverer.repositories

import androidx.paging.PagingData
import com.foobarust.deliverer.data.models.GeolocationPoint
import com.foobarust.deliverer.data.models.OrderBasic
import kotlinx.coroutines.flow.Flow

/**
 * Created by kevin on 3/9/21
 */

interface OrderRepository {

    fun getPendingOrders(sellerId: String): Flow<PagingData<OrderBasic>>

    suspend fun updateOrderLocation(
        idToken: String,
        orderId: String,
        geolocationPoint: GeolocationPoint
    )
}