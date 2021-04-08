package com.foobarust.deliverer.ui.bottomdrawer

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.foobarust.deliverer.R
import com.foobarust.deliverer.data.models.SellerDetail
import com.foobarust.deliverer.data.models.getNormalizedTitle
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.ui.bottomdrawer.SectionPendingListModel.*
import com.foobarust.deliverer.ui.shared.BaseViewModel
import com.foobarust.deliverer.usecases.AuthState
import com.foobarust.deliverer.usecases.auth.GetUserAuthStateUseCase
import com.foobarust.deliverer.usecases.section.GetPendingSectionsUseCase
import com.foobarust.deliverer.usecases.seller.GetSellerDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Created by kevin on 3/8/21
 */

@HiltViewModel
class SectionPendingViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getUserAuthStateUseCase: GetUserAuthStateUseCase,
    getSellerDetailUseCase: GetSellerDetailUseCase,
    getPendingSectionsUseCase: GetPendingSectionsUseCase,
) : BaseViewModel() {

    private val _refreshSectionPending = ConflatedBroadcastChannel(Unit)

    val sellerDetail: StateFlow<SellerDetail?> = _refreshSectionPending
        .asFlow()
        .flatMapLatest { getUserAuthStateUseCase(Unit) }
        .flatMapLatest { authState ->
            when (authState) {
                is AuthState.Authenticated -> getSellerDetailUseCase(authState.data.employedBy!!)
                else -> emptyFlow()
            }
        }
        .map {
            when (it) {
                is Resource.Success -> it.data
                is Resource.Error -> { showToastMessage(it.message); null }
                is Resource.Loading -> null
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    val sectionPendingListModels: Flow<PagingData<SectionPendingListModel>> = _refreshSectionPending
        .asFlow()
        .flatMapLatest { getUserAuthStateUseCase(Unit) }
        .flatMapLatest { authState ->
            when (authState) {
                is AuthState.Authenticated -> getPendingSectionsUseCase(authState.data.employedBy!!)
                else -> emptyFlow()
            }
        }
        .map { pagingData ->
            pagingData.map { sectionBasic ->
                SectionPendingItemModel(
                    sectionId = sectionBasic.id,
                    sectionTitle = sectionBasic.getNormalizedTitle(),
                    sectionDeliveryTime = sectionBasic.deliveryTime,
                    sectionMaxUsers = sectionBasic.maxUsers,
                    sectionJoinedUsersCount = sectionBasic.joinedUsersCount,
                    sectionImageUrl = sectionBasic.imageUrl
                )
            }
        }.map { pagingData ->
            pagingData.insertSeparators { before, after ->
                return@insertSeparators when {
                    before == null && after == null -> SectionPendingEmptyModel(
                        message = context.getString(R.string.section_pending_empty_message),
                        drawableRes = R.drawable.undraw_empty
                    )
                    else -> null
                }
            }
        }.cachedIn(viewModelScope)

    fun onRefreshSectionPending() {
        _refreshSectionPending.offer(Unit)
    }
}