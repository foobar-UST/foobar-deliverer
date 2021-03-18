package com.foobarust.deliverer.repositories

import com.foobarust.deliverer.api.RemoteService
import com.foobarust.deliverer.data.mappers.AuthMapper
import com.foobarust.deliverer.data.models.AuthProfile
import com.foobarust.deliverer.di.ApplicationScope
import com.foobarust.deliverer.usecases.AuthState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Created by kevin on 2/17/21
 */

class AuthRepositoryImpl @Inject constructor(
    @ApplicationScope private val externalScope: CoroutineScope,
    private val firebaseAuth: FirebaseAuth,
    private val remoteService: RemoteService,
    private val authMapper: AuthMapper
): AuthRepository {

    override val authProfileObservable: SharedFlow<AuthState<AuthProfile>> =
        callbackFlow<AuthState<AuthProfile>> {
            channel.offer(AuthState.Loading)

            val listener = FirebaseAuth.AuthStateListener { auth ->
                auth.currentUser?.let {
                    channel.offer(
                        AuthState.Authenticated(authMapper.toAuthProfile(it))
                    )
                } ?: channel.offer(AuthState.Unauthenticated)
            }

            firebaseAuth.addAuthStateListener(listener)

            awaitClose { firebaseAuth.removeAuthStateListener(listener) }
        }
            .shareIn(
                scope = externalScope,
                started = SharingStarted.WhileSubscribed(),
                replay = 1
            )

    override fun isUserSignedIn(): Boolean {
        return firebaseAuth.currentUser != null &&
            firebaseAuth.currentUser?.isAnonymous == false
    }

    override fun getUserId(): String {
        val currentUser = firebaseAuth.currentUser!!
        return currentUser.uid
    }

    override suspend fun getUserIdToken(): String {
        val currentUser = firebaseAuth.currentUser!!
        val tokenResult = currentUser.getIdToken(true).await()
        return tokenResult.token ?: throw Exception("Error getting id token.")
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String) {
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        if (authResult.user == null) {
            throw Exception("Failed to sign in.")
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}