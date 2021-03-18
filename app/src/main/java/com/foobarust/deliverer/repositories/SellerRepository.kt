package com.foobarust.deliverer.repositories

import com.foobarust.deliverer.data.models.SellerDetail

/**
 * Created by kevin on 3/1/21
 */

interface SellerRepository {

    /* Sellers */
    suspend fun getSellerDetail(sellerId: String): SellerDetail
}