package com.foobarust.deliverer.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.foobarust.deliverer.constants.Constants.SELLER_SECTIONS_BASIC_SUB_COLLECTION
import com.foobarust.deliverer.constants.Constants.SELLER_SECTION_AVAILABLE_FIELD
import com.foobarust.deliverer.constants.Constants.SELLER_SECTION_DELIVERY_TIME_FIELD
import com.foobarust.deliverer.constants.Constants.SELLER_SECTION_STATE_FIELD
import com.foobarust.deliverer.constants.Constants.SELLER_SECTION_STATE_PREPARING
import com.foobarust.deliverer.data.dtos.SellerSectionBasicDto
import com.foobarust.deliverer.utils.isNetworkData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.*

/**
 * Created by kevin on 3/9/21
 */

class PendingSectionBasicsPagingSource(
    private val firestore: FirebaseFirestore
) : PagingSource<Query, SellerSectionBasicDto>() {

    private var initialPageQuery: Query? = null

    override fun getRefreshKey(state: PagingState<Query, SellerSectionBasicDto>): Query? = initialPageQuery

    override suspend fun load(params: LoadParams<Query>): LoadResult<Query, SellerSectionBasicDto> {
        return try {
            initialPageQuery = initialPageQuery ?: firestore.collectionGroup(SELLER_SECTIONS_BASIC_SUB_COLLECTION)
                .whereEqualTo(SELLER_SECTION_AVAILABLE_FIELD, true)
                .whereEqualTo(SELLER_SECTION_STATE_FIELD, SELLER_SECTION_STATE_PREPARING)   // Sections that getting prepared
                .whereGreaterThan(SELLER_SECTION_DELIVERY_TIME_FIELD, Date())
                .orderBy(SELLER_SECTION_DELIVERY_TIME_FIELD, Query.Direction.ASCENDING)     // Sort by the oldest sections
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
                data = currentPageData.toObjects(SellerSectionBasicDto::class.java),
                prevKey = null,
                nextKey = nextPageQuery
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}