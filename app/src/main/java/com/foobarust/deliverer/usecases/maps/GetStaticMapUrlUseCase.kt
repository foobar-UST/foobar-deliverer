package com.foobarust.deliverer.usecases.maps

import com.foobarust.deliverer.data.models.GeolocationPoint
import com.foobarust.deliverer.di.MainDispatcher
import com.foobarust.deliverer.repositories.MapRepository
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 1/20/21
 */

class GetStaticMapUrlUseCase @Inject constructor(
    private val mapRepository: MapRepository,
    @MainDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<GeolocationPoint, String>(coroutineDispatcher) {

    override fun execute(parameters: GeolocationPoint): Flow<Resource<String>> = flow {
        val imageUrl = mapRepository.getStaticMapImageUrl(
            centerLocation = parameters
        )
        emit(Resource.Success(imageUrl))
    }
}
