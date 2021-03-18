package com.foobarust.deliverer.api

import com.foobarust.deliverer.constants.Constants.REMOTE_AUTH_HEADER
import com.foobarust.deliverer.data.request.UpdateOrderLocationRequest
import com.foobarust.deliverer.data.request.UpdateUserDetailRequest
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Created by kevin on 3/1/21
 */

interface RemoteService {

    @POST("user/")
    suspend fun updateUserDetail(
        @Header(REMOTE_AUTH_HEADER) idToken: String,
        @Body updateUserDetailRequest: UpdateUserDetailRequest
    )

    @POST("order/deliver/location/")
    suspend fun updateOrderLocation(
        @Header(REMOTE_AUTH_HEADER) idToken: String,
        @Body updateOrderLocationRequest: UpdateOrderLocationRequest
    )
}