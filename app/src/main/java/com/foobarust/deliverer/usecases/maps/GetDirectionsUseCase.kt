package com.foobarust.deliverer.usecases.maps

import com.foobarust.deliverer.data.models.GeolocationPoint
import com.foobarust.deliverer.di.IoDispatcher
import com.foobarust.deliverer.repositories.MapRepository
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 3/8/21
 */

class GetDirectionsUseCase @Inject constructor(
    private val mapRepository: MapRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<GetDirectionsParameters, List<GeolocationPoint>>(coroutineDispatcher) {

    override fun execute(
        parameters: GetDirectionsParameters
    ): Flow<Resource<List<GeolocationPoint>>> = flow {
        val path = mapRepository.getDirectionsPath(
            originLatitude = parameters.latitude,
            originLongitude = parameters.longitude,
            destLatitude = 22.33776,
            destLongitude = 114.26364
        )

        emit(Resource.Success(path))
    }
}

data class GetDirectionsParameters(
    val latitude: Double,
    val longitude: Double
)