package com.foobarust.deliverer.repositories

import android.net.Uri
import com.foobarust.deliverer.api.RemoteService
import com.foobarust.deliverer.constants.Constants.USERS_COLLECTION
import com.foobarust.deliverer.constants.Constants.USER_PHOTOS_STORAGE_FOLDER
import com.foobarust.deliverer.data.mappers.UserMapper
import com.foobarust.deliverer.data.models.UserDetail
import com.foobarust.deliverer.data.request.UpdateUserDetailRequest
import com.foobarust.deliverer.db.UserDetailDao
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.utils.networkCacheResource
import com.foobarust.deliverer.utils.putFileFlow
import com.foobarust.deliverer.utils.snapshotFlow
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by kevin on 2/17/21
 */

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storageReference: StorageReference,
    private val remoteService: RemoteService,
    private val userDetailDao: UserDetailDao,
    private val userMapper: UserMapper
): UserRepository {

    override fun getUserDetailObservable(userId: String): Flow<Resource<UserDetail>> {
        return networkCacheResource(
            cacheSource = {
                userMapper.fromUserDetailCacheDtoToUserDetail(
                    userDetailDao.getUserDetail(userId)
                )
            },
            networkSource = {
                firestore.document("$USERS_COLLECTION/$userId")
                    .snapshotFlow(userMapper::fromUserDetailNetworkDtoToUserDetail, true)
            },
            updateCache = {
                userDetailDao.insertUserDetail(
                    userMapper.toUserDetailCacheDto(it)
                )
            }
        )
    }

    override suspend fun updateUserDetail(idToken: String, name: String?, phoneNum: String?) {
        val request = UpdateUserDetailRequest(
            name = name,
            phoneNum = phoneNum
        )
        remoteService.updateUserDetail(idToken, request)
    }

    override suspend fun removeUserDetailCache() {
        userDetailDao.deleteAll()
    }

    override fun uploadUserPhoto(userId: String, uri: String, extension: String): Flow<Resource<Unit>> {
        val photoFile = Uri.parse(uri)
        val photoFileName = userId + extension
        val photoRef = storageReference.child("$USER_PHOTOS_STORAGE_FOLDER/$photoFileName")

        return photoRef.putFileFlow(photoFile)
    }
}