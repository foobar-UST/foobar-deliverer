package com.foobarust.deliverer.usecases.deliver

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
 * Created by kevin on 3/22/21
 */

class VerifyOrderDeliveredUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val orderRepository: OrderRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<VerifyOrderDeliveredParameters, Unit>(coroutineDispatcher) {

    override fun execute(parameters: VerifyOrderDeliveredParameters): Flow<Resource<Unit>> = flow {
        // Check if the captured content is the verification code
        if (!parameters.isVerified()) {
            throw Exception("Not a verify code.")
        }

        val idToken = if (!authRepository.isUserSignedIn()) {
            throw Exception("Not signed in.")
        } else {
            authRepository.getUserIdToken()
        }

        orderRepository.confirmOrderDelivered(
            idToken = idToken,
            orderId = parameters.orderId
        )

        emit(Resource.Success(Unit))
    }
}

data class VerifyOrderDeliveredParameters(
    val orderId: String,
    val orderVerifyCode: String,
    val capturedContent: String
) {
    fun isVerified(): Boolean = orderVerifyCode == capturedContent
}