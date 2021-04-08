package com.foobarust.deliverer.repositories

import androidx.paging.PagingData
import com.foobarust.deliverer.data.models.*
import com.foobarust.deliverer.states.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Created by kevin on 3/1/21
 */

interface SellerRepository {

    /* Sellers */
    suspend fun getSellerDetail(sellerId: String): SellerDetail

    /* Sections */
    fun getPendingSectionBasics(sellerId: String) : Flow<PagingData<SellerSectionBasic>>

    suspend fun getSectionDetailObservable(sectionId: String): Flow<Resource<SellerSectionDetail>>

    /* Delivery */
    suspend fun applySectionDelivery(idToken: String, sectionId: String)

    suspend fun cancelSectionDelivery(idToken: String, sectionId: String)

    suspend fun completeSectionDelivery(idToken: String, sectionId: String)

    suspend fun updateSectionLocation(
        idToken: String,
        sectionId: String,
        locationPoint: GeolocationPoint,
        travelMode: TravelMode
    )

    suspend fun startSectionPickup(idToken: String, sectionId: String)
}