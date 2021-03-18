package com.foobarust.deliverer.usecases.auth

import com.foobarust.deliverer.di.IoDispatcher
import com.foobarust.deliverer.repositories.AuthRepository
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 8/26/20
 */

class SignInWithEmailAndPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): FlowUseCase<SignInWithEmailAndPasswordParameters, Unit>(dispatcher) {

    override fun execute(
        parameters: SignInWithEmailAndPasswordParameters
    ): Flow<Resource<Unit>> = flow {
        authRepository.signInWithEmailAndPassword(
            email = parameters.email,
            password = parameters.password
        )

        emit(Resource.Success(Unit))
    }
}

data class SignInWithEmailAndPasswordParameters(
    val email: String,
    val password: String
)