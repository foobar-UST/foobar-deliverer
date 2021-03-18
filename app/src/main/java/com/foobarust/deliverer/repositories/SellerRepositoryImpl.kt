package com.foobarust.deliverer.repositories

import com.foobarust.deliverer.constants.Constants.SELLERS_COLLECTION
import com.foobarust.deliverer.data.mappers.SellerMapper
import com.foobarust.deliverer.data.models.SellerDetail
import com.foobarust.deliverer.utils.getAwaitResult
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

/**
 * Created by kevin on 3/1/21
 */

class SellerRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val sellerMapper: SellerMapper
) : SellerRepository {

    override suspend fun getSellerDetail(sellerId: String): SellerDetail {
        return firestore.document("$SELLERS_COLLECTION/$sellerId")
            .getAwaitResult(sellerMapper::toSellerDetail)
    }
}