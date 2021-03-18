package com.foobarust.deliverer.usecases.user

import com.foobarust.deliverer.di.IoDispatcher
import com.foobarust.deliverer.repositories.AuthRepository
import com.foobarust.deliverer.repositories.UserRepository
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 3/8/21
 */

private const val TAG = "UploadUserPhotoUseCase"

class UploadUserPhotoUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<UploadUserPhotoParameters, Unit>(coroutineDispatcher) {

    override fun execute(parameters: UploadUserPhotoParameters): Flow<Resource<Unit>> = flow {
        val userId = if (!authRepository.isUserSignedIn()) {
            throw Exception("Not signed in.")
        } else {
            authRepository.getUserId()
        }

        emitAll(userRepository.uploadUserPhoto(
            userId = userId,
            uri = parameters.photoUri,
            extension = parameters.photoExtension
        ))
    }
}

data class UploadUserPhotoParameters(
    val photoUri: String,
    val photoExtension: String
)