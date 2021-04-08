package com.foobarust.deliverer.usecases.user

import com.foobarust.deliverer.data.models.UserDelivery
import com.foobarust.deliverer.di.IoDispatcher
import com.foobarust.deliverer.repositories.AuthRepository
import com.foobarust.deliverer.repositories.UserRepository
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 4/7/21
 */

class GetUserInfoUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<String, UserDelivery>(coroutineDispatcher) {

    override fun execute(parameters: String): Flow<Resource<UserDelivery>> = flow {
        if (!authRepository.isUserSignedIn()) {
            throw Exception("Not signed in.")
        }

        val userDelivery = userRepository.getUserDeliveryProfile(userId = parameters)

        emit(Resource.Success(userDelivery))
    }
}