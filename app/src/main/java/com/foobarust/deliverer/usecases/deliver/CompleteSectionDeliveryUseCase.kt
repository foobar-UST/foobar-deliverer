package com.foobarust.deliverer.usecases.deliver

import com.foobarust.deliverer.di.IoDispatcher
import com.foobarust.deliverer.repositories.AuthRepository
import com.foobarust.deliverer.repositories.SellerRepository
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 4/8/21
 */

class CompleteSectionDeliveryUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<String, Unit>(coroutineDispatcher) {

    override fun execute(parameters: String): Flow<Resource<Unit>> = flow {
        val idToken = if (!authRepository.isUserSignedIn()) {
            throw Exception("Not signed in.")
        } else {
            authRepository.getUserIdToken()
        }

        sellerRepository.completeSectionDelivery(
            idToken = idToken,
            sectionId = parameters
        )

        emit(Resource.Success(Unit))
    }
}