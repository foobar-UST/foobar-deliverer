package com.foobarust.deliverer.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.foobarust.deliverer.constants.Constants.ORDERS_BASIC_COLLECTION
import com.foobarust.deliverer.constants.Constants.ORDER_CREATED_AT_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_SELLER_ID_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_STATE_FIELD
import com.foobarust.deliverer.constants.Constants.ORDER_STATE_PREPARING
import com.foobarust.deliverer.constants.Constants.ORDER_TYPE_FIELD
import com.foobarust.deliverer.data.dtos.OrderBasicDto
import com.foobarust.deliverer.utils.isNetworkData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Created by kevin on 3/9/21
 */

class OrderPendingPagingSource(
    private val firestore: FirebaseFirestore,
    private val sellerId: String
) : PagingSource<Query, OrderBasicDto>() {

    private var initialPageQuery: Query? = null

    override fun getRefreshKey(state: PagingState<Query, OrderBasicDto>): Query? = initialPageQuery

    override suspend fun load(params: LoadParams<Query>): LoadResult<Query, OrderBasicDto> {
        return try {
            initialPageQuery = initialPageQuery ?: firestore.collection(ORDERS_BASIC_COLLECTION)
                .whereEqualTo(ORDER_SELLER_ID_FIELD, sellerId)
                .whereEqualTo(ORDER_TYPE_FIELD, 1)  // Off-campus orders
                .whereEqualTo(ORDER_STATE_FIELD, ORDER_STATE_PREPARING)
                .orderBy(ORDER_CREATED_AT_FIELD, Query.Direction.ASCENDING) // List the oldest orders first
                .limit(params.loadSize.toLong())

            val currentPageQuery = params.key ?: initialPageQuery!!
            val currentPageData = currentPageQuery.get().await()

            val nextPageQuery = if (!currentPageData.isEmpty) {
                val lastVisibleItem = currentPageData.documents[currentPageData.size() - 1]
                initialPageQuery!!.startAfter(lastVisibleItem)
            } else {
                null
            }

            if (!currentPageData.isNetworkData()) {
                throw Exception("Network error.")
            }

            LoadResult.Page(
                data = currentPageData.toObjects(OrderBasicDto::class.java),
                prevKey = null,
                nextKey = nextPageQuery
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}