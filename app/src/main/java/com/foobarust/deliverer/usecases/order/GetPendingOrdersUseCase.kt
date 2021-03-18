package com.foobarust.deliverer.usecases.order

import androidx.paging.PagingData
import com.foobarust.deliverer.data.models.OrderBasic
import com.foobarust.deliverer.di.IoDispatcher
import com.foobarust.deliverer.repositories.AuthRepository
import com.foobarust.deliverer.repositories.OrderRepository
import com.foobarust.deliverer.usecases.PagingUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 3/9/21
 */

class GetPendingOrdersUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val orderRepository: OrderRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : PagingUseCase<String, OrderBasic>(coroutineDispatcher) {

    override fun execute(parameters: String): Flow<PagingData<OrderBasic>> = flow {
        if (!authRepository.isUserSignedIn()) {
            throw Exception("Not signed in.")
        }

        emitAll(orderRepository.getPendingOrders(sellerId = parameters))
    }
}