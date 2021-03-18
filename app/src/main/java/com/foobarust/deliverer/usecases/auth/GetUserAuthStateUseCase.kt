package com.foobarust.deliverer.usecases.auth

import com.foobarust.deliverer.data.models.AuthProfile
import com.foobarust.deliverer.data.models.UserDetail
import com.foobarust.deliverer.data.models.isDeliveryVerified
import com.foobarust.deliverer.di.ApplicationScope
import com.foobarust.deliverer.di.IoDispatcher
import com.foobarust.deliverer.repositories.AuthRepository
import com.foobarust.deliverer.repositories.UserRepository
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.usecases.AuthState
import com.foobarust.deliverer.usecases.AuthUseCase
import com.foobarust.deliverer.utils.cancelIfActive
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by kevin on 2/6/21
 */

private const val TAG = "GetUserAuthStateUseCase"

@Singleton
class GetUserAuthStateUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    @ApplicationScope private val externalScope: CoroutineScope,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : AuthUseCase<Unit, UserDetail>(coroutineDispatcher) {

    private var observeUserDetailJob: Job? = null

    private val authFlow: SharedFlow<AuthState<UserDetail>> = channelFlow<AuthState<UserDetail>> {
        authRepository.authProfileObservable.collect { authState ->
            stopObserveUserDetail()
            when (authState) {
                is AuthState.Authenticated -> {
                    println("[$TAG]: User is signed in. Start observe UserDetail.")
                    startObserveUserDetail(authProfile = authState.data)
                }
                AuthState.Unauthenticated -> {
                    println("[$TAG]: User is signed out.")
                    channel.offer(AuthState.Unauthenticated)
                }
                AuthState.Unverified -> Unit
                AuthState.Loading -> {
                    println("[$TAG]: Loading auth profile...")
                    channel.offer(AuthState.Loading)
                }
            }
        }
    }.shareIn(
        scope = externalScope,
        started = SharingStarted.Eagerly,
        replay = 1
    )

    override fun execute(parameters: Unit): Flow<AuthState<UserDetail>> = authFlow

    private fun ProducerScope<AuthState<UserDetail>>.startObserveUserDetail(authProfile: AuthProfile) {
        observeUserDetailJob = externalScope.launch(coroutineDispatcher) {
            userRepository.getUserDetailObservable(authProfile.id).collect {
                when (it) {
                    is Resource.Success -> {
                        // Offer user detail data from network db or local cache.
                        val userDetail = it.data
                        if (userDetail.isDeliveryVerified()) {
                            println("[$TAG]: Network available, offered UserDetail.")
                            channel.offer(AuthState.Authenticated(userDetail))
                        } else {
                            println("[$TAG]: Network available, user unverified.")
                            channel.offer(AuthState.Unverified)
                        }
                    }
                    is Resource.Error -> {
                        println("[$TAG]: Network unavailable.")
                        channel.offer(AuthState.Unauthenticated)
                    }
                    is Resource.Loading -> Unit
                }
            }
        }
    }

    private fun stopObserveUserDetail() {
        println("[$TAG]: Stop observing UserDetail.")
        observeUserDetailJob.cancelIfActive()
    }
}