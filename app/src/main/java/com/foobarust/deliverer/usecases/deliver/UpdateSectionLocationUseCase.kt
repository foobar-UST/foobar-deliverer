package com.foobarust.deliverer.usecases.deliver

import com.foobarust.deliverer.data.models.GeolocationPoint
import com.foobarust.deliverer.data.models.TravelMode
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
 * Created by kevin on 3/10/21
 */

class UpdateSectionLocationUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<UpdateSectionLocationParameters, Unit>(coroutineDispatcher) {

    override fun execute(parameters: UpdateSectionLocationParameters): Flow<Resource<Unit>> = flow {
        val idToken = if (authRepository.isUserSignedIn()) {
            authRepository.getUserIdToken()
        } else {
            throw Exception("Not signed in.")
        }

        sellerRepository.updateSectionLocation(
            idToken = idToken,
            sectionId = parameters.sectionId,
            locationPoint = parameters.locationPoint,
            travelMode = parameters.travelMode
        )

        emit(Resource.Success(Unit))
    }
}

data class UpdateSectionLocationParameters(
    val sectionId: String,
    val locationPoint: GeolocationPoint,
    val travelMode: TravelMode
)
