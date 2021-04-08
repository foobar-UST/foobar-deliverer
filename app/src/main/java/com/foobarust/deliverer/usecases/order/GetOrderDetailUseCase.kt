package com.foobarust.deliverer.usecases.order

import com.foobarust.deliverer.data.models.OrderDetail
import com.foobarust.deliverer.di.IoDispatcher
import com.foobarust.deliverer.repositories.AuthRepository
import com.foobarust.deliverer.repositories.OrderRepository
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 4/3/21
 */

class GetOrderDetailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val orderRepository: OrderRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<String, OrderDetail>(coroutineDispatcher) {

    override fun execute(parameters: String): Flow<Resource<OrderDetail>> = flow {
        if (!authRepository.isUserSignedIn()) {
            throw Exception("Not signed in.")
        }

        emitAll(orderRepository.getOrderDetailObservable(orderId = parameters))
    }
}