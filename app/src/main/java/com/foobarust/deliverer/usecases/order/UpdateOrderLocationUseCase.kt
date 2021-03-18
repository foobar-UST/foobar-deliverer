package com.foobarust.deliverer.usecases.order

import com.foobarust.deliverer.data.models.GeolocationPoint
import com.foobarust.deliverer.di.IoDispatcher
import com.foobarust.deliverer.repositories.AuthRepository
import com.foobarust.deliverer.repositories.OrderRepository
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 3/10/21
 */

class UpdateOrderLocationUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val orderRepository: OrderRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<UpdateOrderLocationParameters, Unit>(coroutineDispatcher) {

    override fun execute(parameters: UpdateOrderLocationParameters): Flow<Resource<Unit>> = flow {
        val idToken = if (!authRepository.isUserSignedIn()) {
            throw Exception("Not signed in.")
        } else {
            authRepository.getUserIdToken()
        }

        orderRepository.updateOrderLocation(
            idToken = idToken,
            orderId = parameters.orderId,
            geolocationPoint = parameters.geolocationPoint
        )
    }
}

data class UpdateOrderLocationParameters(
    val orderId: String,
    val geolocationPoint: GeolocationPoint
)