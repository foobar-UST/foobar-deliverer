package com.foobarust.deliverer.usecases.order

import com.foobarust.deliverer.data.models.SectionOrderWithUserPublic
import com.foobarust.deliverer.di.IoDispatcher
import com.foobarust.deliverer.repositories.AuthRepository
import com.foobarust.deliverer.repositories.OrderRepository
import com.foobarust.deliverer.repositories.UserRepository
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

/**
 * Created by kevin on 4/3/21
 */

class GetSectionOrdersUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<String, List<SectionOrderWithUserPublic>>(coroutineDispatcher) {

    override fun execute(parameters: String): Flow<Resource<List<SectionOrderWithUserPublic>>> = flow {
        if (!authRepository.isUserSignedIn()) {
            throw Exception("Not signed in.")
        }

        val resultFlow = orderRepository.getOrderBasicsObservable(sectionId = parameters)
            .mapLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val orderBasics = resource.data
                        val sectionOrderWithUserPublics = orderBasics
                            .map {
                                val userPublic = userRepository.getUserPublicProfile(it.userId)
                                SectionOrderWithUserPublic(it, userPublic)
                            }
                            .sortedBy {
                                // Show undelivered orders first
                                it.orderBasic.state.precedence
                            }
                        Resource.Success(sectionOrderWithUserPublics)
                    }
                    is Resource.Error -> Resource.Error(resource.message)
                    is Resource.Loading -> Resource.Loading()
                }
            }

        emitAll(resultFlow)
    }
}