package com.foobarust.deliverer.usecases.section

import androidx.paging.PagingData
import com.foobarust.deliverer.data.models.SellerSectionBasic
import com.foobarust.deliverer.di.IoDispatcher
import com.foobarust.deliverer.repositories.AuthRepository
import com.foobarust.deliverer.repositories.SellerRepository
import com.foobarust.deliverer.usecases.PagingUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 3/9/21
 */

class GetPendingSectionsUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : PagingUseCase<String, SellerSectionBasic>(coroutineDispatcher) {

    override fun execute(parameters: String): Flow<PagingData<SellerSectionBasic>> = flow {
        if (!authRepository.isUserSignedIn()) {
            throw Exception("Not signed in.")
        }

        emitAll(
            sellerRepository.getPendingSectionBasics(sellerId = parameters)
        )
    }
}