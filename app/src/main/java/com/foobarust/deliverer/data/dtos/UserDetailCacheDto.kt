package com.foobarust.deliverer.data.dtos

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.foobarust.deliverer.constants.Constants
import com.foobarust.deliverer.constants.Constants.USERS_CACHE_ENTITY
import com.foobarust.deliverer.constants.Constants.USER_EMAIL_FIELD
import com.foobarust.deliverer.constants.Constants.USER_EMPLOYED_BY_FIELD
import com.foobarust.deliverer.constants.Constants.USER_ID_FIELD
import com.foobarust.deliverer.constants.Constants.USER_NAME_FIELD
import com.foobarust.deliverer.constants.Constants.USER_PHONE_NUM_FIELD
import com.foobarust.deliverer.constants.Constants.USER_PHOTO_URL_FIELD
import com.foobarust.deliverer.constants.Constants.USER_ROLES_FIELD
import com.foobarust.deliverer.constants.Constants.USER_SECTION_IN_DELIVERY
import com.foobarust.deliverer.constants.Constants.USER_UPDATED_AT_FIELD
import com.foobarust.deliverer.constants.Constants.USER_USERNAME_FIELD
import com.google.firebase.firestore.PropertyName
import java.util.*

@Entity(tableName = USERS_CACHE_ENTITY)
data class UserDetailCacheDto(
    @PrimaryKey
    @ColumnInfo(name = USER_ID_FIELD)
    val id: String,

    @ColumnInfo(name = USER_NAME_FIELD)
    val name: String? = null,

    @ColumnInfo(name = USER_USERNAME_FIELD)
    val username: String? = null,

    @ColumnInfo(name = USER_EMAIL_FIELD)
    val email: String? = null,

    @ColumnInfo(name = USER_PHONE_NUM_FIELD)
    val phoneNum: String? = null,

    @ColumnInfo(name = USER_PHOTO_URL_FIELD)
    val photoUrl: String? = null,

    @ColumnInfo(name = USER_ROLES_FIELD)
    val roles: String? = null,

    @ColumnInfo(name = USER_UPDATED_AT_FIELD)
    val updatedAt: Date? = null,

    @PropertyName(Constants.USER_CREATED_REST_FIELD)
    val createdRest: Boolean? = null,

    @ColumnInfo(name = USER_EMPLOYED_BY_FIELD)
    val employedBy: String? = null,

    @ColumnInfo(name = USER_SECTION_IN_DELIVERY)
    val sectionInDelivery: String? = null
)