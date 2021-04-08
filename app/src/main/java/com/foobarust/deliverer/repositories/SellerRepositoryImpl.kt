package com.foobarust.deliverer.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.foobarust.deliverer.api.RemoteService
import com.foobarust.deliverer.constants.Constants.SELLERS_COLLECTION
import com.foobarust.deliverer.constants.Constants.SELLER_SECTIONS_SUB_COLLECTION
import com.foobarust.deliverer.constants.Constants.SELLER_SECTION_ID_FIELD
import com.foobarust.deliverer.data.mappers.SellerMapper
import com.foobarust.deliverer.data.models.*
import com.foobarust.deliverer.data.paging.PendingSectionBasicsPagingSource
import com.foobarust.deliverer.data.request.ApplySectionDeliveryRequest
import com.foobarust.deliverer.data.request.CancelSectionDeliveryRequest
import com.foobarust.deliverer.data.request.CompleteSectionDeliveryRequest
import com.foobarust.deliverer.data.request.StartSectionPickupRequest
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.utils.getAwaitResult
import com.foobarust.deliverer.utils.snapshotFlow
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Created by kevin on 3/1/21
 */

private const val PENDING_SECTIONS_PAGE_SIZE = 10

class SellerRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val remoteService: RemoteService,
    private val sellerMapper: SellerMapper
) : SellerRepository {

    override suspend fun getSellerDetail(sellerId: String): SellerDetail {
        return firestore.document("$SELLERS_COLLECTION/$sellerId")
            .getAwaitResult(sellerMapper::toSellerDetail)
    }

    override fun getPendingSectionBasics(sellerId: String): Flow<PagingData<SellerSectionBasic>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = PENDING_SECTIONS_PAGE_SIZE * 2,
                pageSize = PENDING_SECTIONS_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PendingSectionBasicsPagingSource(firestore) }
        )
            .flow
            .map { pagingData ->
                pagingData.map { sellerMapper.toSellerSectionBasic(it) }
            }
    }

    override suspend fun getSectionDetailObservable(sectionId: String): Flow<Resource<SellerSectionDetail>> {
        return firestore.collectionGroup(SELLER_SECTIONS_SUB_COLLECTION)
            .whereEqualTo(SELLER_SECTION_ID_FIELD, sectionId)
            .snapshotFlow(sellerMapper::toSellerSectionDetail, keepAlive = true)
            .map {
                when (it) {
                   is Resource.Success -> Resource.Success(it.data.first())
                   is Resource.Error -> Resource.Error(it.message)
                   is Resource.Loading -> Resource.Loading()
                }
            }
    }

    override suspend fun applySectionDelivery(idToken: String, sectionId: String) {
        return remoteService.applySectionDelivery(
            idToken = idToken,
            applySectionDeliveryRequest = ApplySectionDeliveryRequest(sectionId)
        )
    }

    override suspend fun cancelSectionDelivery(idToken: String, sectionId: String) {
        return remoteService.cancelSectionDelivery(
            idToken = idToken,
            cancelSectionDeliveryRequest = CancelSectionDeliveryRequest(sectionId)
        )
    }

    override suspend fun completeSectionDelivery(idToken: String, sectionId: String) {
        return remoteService.completeSectionDelivery(
            idToken = idToken,
            completeSectionDeliveryRequest = CompleteSectionDeliveryRequest(sectionId)
        )
    }

    override suspend fun updateSectionLocation(
        idToken: String,
        sectionId: String,
        locationPoint: GeolocationPoint,
        travelMode: TravelMode
    ) {
        val request = sellerMapper.toUpdateSectionLocationRequest(
            sectionId = sectionId,
            geolocationPoint = locationPoint,
            travelMode = travelMode
        )

        remoteService.updateSectionLocation(
            idToken = idToken,
            updateSectionLocationRequest = request
        )
    }

    override suspend fun startSectionPickup(idToken: String, sectionId: String) {
        remoteService.startSectionPickup(
            idToken = idToken,
            startSectionPickupRequest = StartSectionPickupRequest(sectionId)
        )
    }
}