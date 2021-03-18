package com.foobarust.deliverer.data.mappers

import com.foobarust.deliverer.constants.Constants.USER_ROLE_DELIVERER
import com.foobarust.deliverer.constants.Constants.USER_ROLE_SELLER
import com.foobarust.deliverer.constants.Constants.USER_ROLE_USER
import com.foobarust.deliverer.data.dtos.UserDetailCacheDto
import com.foobarust.deliverer.data.dtos.UserDetailNetworkDto
import com.foobarust.deliverer.data.models.UserDetail
import com.foobarust.deliverer.data.models.UserRole
import javax.inject.Inject

class UserMapper @Inject constructor() {

    fun fromUserDetailNetworkDtoToUserDetail(networkDto: UserDetailNetworkDto): UserDetail {
        return UserDetail(
            id = networkDto.id!!,
            username = networkDto.username!!,
            email = networkDto.email!!,
            name = networkDto.name,
            phoneNum = networkDto.phoneNum,
            photoUrl = networkDto.photoUrl,
            roles = networkDto.roles?.map { mapUserRole(it) } ?: emptyList(),
            updatedAt = networkDto.updatedAt?.toDate(),
            createdRest = networkDto.createdRest ?: false,
            employedBy = networkDto.employedBy
        )
    }

    fun fromUserDetailCacheDtoToUserDetail(cacheDto: UserDetailCacheDto): UserDetail {
        val userRoles = cacheDto.roles
            ?.split(",")
            ?.map { mapUserRole(it) }
            ?: emptyList()

        return UserDetail(
            id = cacheDto.id,
            username = cacheDto.username!!,
            email = cacheDto.email!!,
            name = cacheDto.name,
            phoneNum = cacheDto.phoneNum,
            photoUrl = cacheDto.photoUrl,
            roles = userRoles,
            updatedAt = cacheDto.updatedAt,
            createdRest = cacheDto.createdRest ?: false,
            employedBy = cacheDto.employedBy
        )
    }

    fun toUserDetailCacheDto(userDetail: UserDetail): UserDetailCacheDto {
        return UserDetailCacheDto(
            id = userDetail.id,
            name = userDetail.name,
            username = userDetail.username,
            email = userDetail.email,
            phoneNum = userDetail.phoneNum,
            photoUrl = userDetail.photoUrl,
            roles = userDetail.roles.joinToString(",") { mapRoleString(it) },
            updatedAt = userDetail.updatedAt,
            createdRest = userDetail.createdRest,
            employedBy = userDetail.employedBy
        )
    }

    private fun mapRoleString(userRole: UserRole): String {
        return when (userRole) {
            UserRole.USER -> USER_ROLE_USER
            UserRole.SELLER -> USER_ROLE_SELLER
            UserRole.DELIVERER -> USER_ROLE_DELIVERER
        }
    }

    private fun mapUserRole(role: String): UserRole {
        return when (role) {
            USER_ROLE_USER -> UserRole.USER
            USER_ROLE_SELLER -> UserRole.SELLER
            USER_ROLE_DELIVERER -> UserRole.DELIVERER
            else -> throw IllegalStateException("Unknown user role: $role")
        }
    }
}