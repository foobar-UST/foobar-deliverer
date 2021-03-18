package com.foobarust.deliverer.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.foobarust.deliverer.api.RemoteService
import com.foobarust.deliverer.data.mappers.OrderMapper
import com.foobarust.deliverer.data.models.GeolocationPoint
import com.foobarust.deliverer.data.models.OrderBasic
import com.foobarust.deliverer.data.paging.OrderPendingPagingSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Created by kevin on 3/9/21
 */

private const val PENDING_ORDERS_PAGE_SIZE = 10

class OrderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val remoteService: RemoteService,
    private val orderMapper: OrderMapper
) : OrderRepository {

    override fun getPendingOrders(sellerId: String): Flow<PagingData<OrderBasic>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = PENDING_ORDERS_PAGE_SIZE * 2,
                pageSize = PENDING_ORDERS_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { OrderPendingPagingSource(firestore, sellerId) }
        )
            .flow
            .map { pagingData ->
                pagingData.map { orderMapper.toOrderBasic(it) }
            }
    }

    override suspend fun updateOrderLocation(
        idToken: String,
        orderId: String,
        geolocationPoint: GeolocationPoint
    ) {
        remoteService.updateOrderLocation(
            idToken = idToken,
            updateOrderLocationRequest = orderMapper.toUpdateOrderLocationRequest(
                orderId = orderId,
                geolocationPoint = geolocationPoint
            )
        )
    }
}