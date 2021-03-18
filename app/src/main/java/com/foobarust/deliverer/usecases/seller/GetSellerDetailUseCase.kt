package com.foobarust.deliverer.usecases.seller

import com.foobarust.deliverer.data.models.SellerDetail
import com.foobarust.deliverer.di.IoDispatcher
import com.foobarust.deliverer.repositories.SellerRepository
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 3/8/21
 */

class GetSellerDetailUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<String, SellerDetail>(coroutineDispatcher) {

    override fun execute(parameters: String): Flow<Resource<SellerDetail>> = flow {
        val sellerDetail = sellerRepository.getSellerDetail(sellerId = parameters)
        emit(Resource.Success(sellerDetail))
    }
}