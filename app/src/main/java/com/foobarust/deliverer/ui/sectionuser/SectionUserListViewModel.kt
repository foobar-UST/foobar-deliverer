package com.foobarust.deliverer.ui.sectionuser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.deliverer.data.models.OrderState
import com.foobarust.deliverer.data.models.SectionOrderWithUserPublic
import com.foobarust.deliverer.data.models.UserDetail
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.ui.sectionuser.SectionUserListModel.SectionUserListHeaderModel
import com.foobarust.deliverer.ui.sectionuser.SectionUserListModel.SectionUserListItemModel
import com.foobarust.deliverer.usecases.AuthState
import com.foobarust.deliverer.usecases.auth.GetUserAuthStateUseCase
import com.foobarust.deliverer.usecases.order.GetSectionOrdersUseCase
import com.foobarust.deliverer.utils.cancelIfActive
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 4/2/21
 */

@HiltViewModel
class SectionUserListViewModel @Inject constructor(
    private val getUserAuthStateUseCase: GetUserAuthStateUseCase,
    private val getSectionOrdersUseCase: GetSectionOrdersUseCase
) : ViewModel() {

    private val _sectionUserListUiState = ConflatedBroadcastChannel<SectionUserListUiState>(
        SectionUserListUiState.Loading
    )
    val sectionUserListUiState: Flow<SectionUserListUiState> = _sectionUserListUiState.asFlow()

    private val _sectionUserListModels = MutableStateFlow<List<SectionUserListModel>>(emptyList())
    val sectionUserListModels: StateFlow<List<SectionUserListModel>> = _sectionUserListModels.asStateFlow()

    private var fetchSectionUsersJob: Job? = null

    private val deliveredOrderStates = listOf(OrderState.DELIVERED, OrderState.ARCHIVED)

    init {
        onRefreshSectionUsers()
    }

    fun onRefreshSectionUsers(isSwipeRefresh: Boolean = false) {
        fetchSectionUsersJob?.cancelIfActive()
        fetchSectionUsersJob = viewModelScope.launch {
            getUserAuthStateUseCase(Unit)
                .filterIsInstance<AuthState.Authenticated<UserDetail>>()
                .map { it.data.sectionInDelivery }
                .flatMapLatest { sectionId ->
                    sectionId?.let { getSectionOrdersUseCase(it) } ?: emptyFlow()
                }
                .collectLatest {
                    when (it) {
                        is Resource.Success -> {
                            _sectionUserListUiState.offer(SectionUserListUiState.Success)
                            _sectionUserListModels.value = buildSectionUserListModels(it.data)
                        }
                        is Resource.Error -> {
                            _sectionUserListUiState.offer(SectionUserListUiState.Error(it.message))
                        }
                        is Resource.Loading -> {
                            if (!isSwipeRefresh) {
                                _sectionUserListUiState.offer(SectionUserListUiState.Loading)
                            }
                        }
                    }
                }
        }
    }

    private fun buildSectionUserListModels(
        sectionOrderWithUserPublics: List<SectionOrderWithUserPublic>
    ): List<SectionUserListModel> {
        val deliveredOrders = sectionOrderWithUserPublics.filter {
            it.orderBasic.state in deliveredOrderStates
        }.map {
            toSectionUserListItemModel(it)
        }

        val pendingOrders = sectionOrderWithUserPublics.filter {
            it.orderBasic.state !in deliveredOrderStates
        }.map {
            toSectionUserListItemModel(it)
        }

        return buildList {
            addAll(pendingOrders)
            if (deliveredOrders.isNotEmpty()) {
                add(SectionUserListHeaderModel)
                addAll(deliveredOrders)
            }
        }
    }

    private fun toSectionUserListItemModel(
        sectionOrderWithUserPublic: SectionOrderWithUserPublic
    ): SectionUserListItemModel {
        return SectionUserListItemModel(
            orderId = sectionOrderWithUserPublic.orderBasic.id,
            orderIdentifier = sectionOrderWithUserPublic.orderBasic.identifier,
            userId = sectionOrderWithUserPublic.userPublic.id,
            username = sectionOrderWithUserPublic.userPublic.username,
            userPhotoUrl = sectionOrderWithUserPublic.userPublic.photoUrl,
            orderItemsCount = sectionOrderWithUserPublic.orderBasic.orderItemsCount,
            orderState = sectionOrderWithUserPublic.orderBasic.state
        )
    }
}

sealed class SectionUserListUiState {
    object Success : SectionUserListUiState()
    data class Error(val message: String?) : SectionUserListUiState()
    object Loading : SectionUserListUiState()
}