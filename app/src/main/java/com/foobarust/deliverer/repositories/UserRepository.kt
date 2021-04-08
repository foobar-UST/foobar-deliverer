package com.foobarust.deliverer.repositories

import com.foobarust.deliverer.data.models.UserDelivery
import com.foobarust.deliverer.data.models.UserDetail
import com.foobarust.deliverer.data.models.UserPublic
import com.foobarust.deliverer.states.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Created by kevin on 3/1/21
 */

interface UserRepository {

    /* User detail */
    fun getUserDetailObservable(userId: String): Flow<Resource<UserDetail>>

    suspend fun updateUserDetail(idToken: String, name: String?, phoneNum: String?)

    suspend fun removeUserDetailCache()

    fun uploadUserPhoto(userId: String, uri: String, extension: String): Flow<Resource<Unit>>

    suspend fun getUserPublicProfile(userId: String): UserPublic

    suspend fun getUserDeliveryProfile(userId: String): UserDelivery
}