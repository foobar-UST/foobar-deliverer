package com.foobarust.deliverer.api

import com.foobarust.deliverer.constants.Constants.REMOTE_AUTH_HEADER
import com.foobarust.deliverer.data.request.*
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

    @POST("section/deliver/")
    suspend fun applySectionDelivery(
        @Header(REMOTE_AUTH_HEADER) idToken: String,
        @Body applySectionDeliveryRequest: ApplySectionDeliveryRequest
    )

    @POST("section/cancel-delivery/")
    suspend fun cancelSectionDelivery(
        @Header(REMOTE_AUTH_HEADER) idToken: String,
        @Body cancelSectionDeliveryRequest: CancelSectionDeliveryRequest
    )

    @POST("section/complete-delivery/")
    suspend fun completeSectionDelivery(
        @Header(REMOTE_AUTH_HEADER) idToken: String,
        @Body completeSectionDeliveryRequest: CompleteSectionDeliveryRequest
    )

    @POST("section/location/")
    suspend fun updateSectionLocation(
        @Header(REMOTE_AUTH_HEADER) idToken: String,
        @Body updateSectionLocationRequest: UpdateSectionLocationRequest
    )

    @POST("section/pickup/")
    suspend fun startSectionPickup(
        @Header(REMOTE_AUTH_HEADER) idToken: String,
        @Body startSectionPickupRequest: StartSectionPickupRequest
    )

    @POST("order/delivered")
    suspend fun confirmOrderDelivered(
        @Header(REMOTE_AUTH_HEADER) idToken: String,
        @Body confirmOrderDeliveredRequest: ConfirmOrderDeliveredRequest
    )
}