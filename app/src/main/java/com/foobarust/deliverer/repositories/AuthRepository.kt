package com.foobarust.deliverer.repositories

import com.foobarust.deliverer.data.models.AuthProfile
import com.foobarust.deliverer.usecases.AuthState
import kotlinx.coroutines.flow.SharedFlow

/**
 * Created by kevin on 3/1/21
 */

interface AuthRepository {

    val authProfileObservable: SharedFlow<AuthState<AuthProfile>>

    fun isUserSignedIn(): Boolean

    fun getUserId(): String

    suspend fun getUserIdToken(): String

    suspend fun signInWithEmailAndPassword(email: String, password: String)

    fun signOut()
}