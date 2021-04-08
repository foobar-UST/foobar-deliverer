package com.foobarust.deliverer.usecases.section

import com.foobarust.deliverer.data.models.SellerSectionDetail
import com.foobarust.deliverer.di.IoDispatcher
import com.foobarust.deliverer.repositories.SellerRepository
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by kevin on 12/27/20
 */

@Singleton
class GetSectionDetailUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<String, SellerSectionDetail>(coroutineDispatcher) {

    override fun execute(parameters: String): Flow<Resource<SellerSectionDetail>> = flow {
        val sectionDetail = sellerRepository.getSectionDetailObservable(sectionId = parameters)
        emitAll(sectionDetail)
    }
}