package com.foobarust.deliverer.ui.sectionuser

import android.os.Parcelable
import androidx.lifecycle.viewModelScope
import com.foobarust.deliverer.data.models.OrderDetail
import com.foobarust.deliverer.data.models.getNormalizedTitle
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.ui.sectionuser.SectionUserDetailListModel.*
import com.foobarust.deliverer.ui.shared.BaseViewModel
import com.foobarust.deliverer.usecases.deliver.ConfirmOrderDeliveredUseCase
import com.foobarust.deliverer.usecases.order.GetOrderDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

/**
 * Created by kevin on 4/2/21
 */

@HiltViewModel
class SectionUserDetailViewModel @Inject constructor(
    private val getOrderDetailUseCase: GetOrderDetailUseCase,
    private val confirmOrderDeliveredUseCase: ConfirmOrderDeliveredUseCase,
    private val paymentMethodUtil: PaymentMethodUtil
) : BaseViewModel() {

    private val _sectionUserDetailProperty = ConflatedBroadcastChannel<SectionUserDetailProperty?>()

    private val _orderDetail = MutableStateFlow<OrderDetail?>(null)
    val orderDetail: StateFlow<OrderDetail?> = _orderDetail.asStateFlow()

    private val _sectionUserDetailUiState = MutableStateFlow<SectionUserDetailUiState>(
        SectionUserDetailUiState.Loading
    )
    val sectionUserDetailUiState: StateFlow<SectionUserDetailUiState> = _sectionUserDetailUiState
        .asStateFlow()

    private val _sectionUserDetailListModels = MutableStateFlow<List<SectionUserDetailListModel>>(emptyList())
    val sectionUserDetailListModels: StateFlow<List<SectionUserDetailListModel>> = _sectionUserDetailListModels
        .asStateFlow()

    private val _finishSwipeRefresh = Channel<Unit>()
    val finishSwipeRefresh: Flow<Unit> = _finishSwipeRefresh.receiveAsFlow()

    init {
        viewModelScope.launch {
            _sectionUserDetailProperty.asFlow().flatMapLatest { orderId ->
                orderId?.let { getOrderDetailUseCase(it.orderId) } ?: emptyFlow()
            }
            .collectLatest {
                when (it) {
                    is Resource.Success -> {
                        _orderDetail.value = it.data
                        _sectionUserDetailUiState.value = SectionUserDetailUiState.Success
                        _sectionUserDetailListModels.value = buildSectionUserDetailListModels(it.data)
                        _finishSwipeRefresh.offer(Unit)
                    }
                    is Resource.Error -> {
                        _sectionUserDetailUiState.value = SectionUserDetailUiState.Error(it.message)
                        _finishSwipeRefresh.offer(Unit)
                    }
                    is Resource.Loading -> {
                        _sectionUserDetailUiState.value = SectionUserDetailUiState.Loading
                    }
                }
            }
        }
    }

    fun onFetchOrderDetail(property: SectionUserDetailProperty) {
        _sectionUserDetailProperty.offer(property)
    }

    fun onConfirmOrderDelivered() = viewModelScope.launch {
        val orderId = _orderDetail.value?.id ?: return@launch
        confirmOrderDeliveredUseCase(orderId).collect {
            when (it) {
                is Resource.Success -> Unit
                is Resource.Error -> showToastMessage(it.message)
                is Resource.Loading -> Unit
            }
        }
    }

    private fun buildSectionUserDetailListModels(
        orderDetail: OrderDetail
    ): List<SectionUserDetailListModel> = buildList {
        if (!orderDetail.isPaid) {
            add(SectionUserDetailPaymentNoticeModel)
        }

        add(SectionUserDetailOrderInfoModel(
            orderIdentifier = orderDetail.identifier,
            orderTitle = orderDetail.getNormalizedTitle(),
            orderMessage = orderDetail.message,
            orderImageUrl = orderDetail.imageUrl,
            orderState = orderDetail.state,
            totalCost = orderDetail.totalCost,
            userId = orderDetail.userId,
            isPaid = orderDetail.isPaid
        ))

        orderDetail.orderItems.map { orderItem ->
            SectionUserDetailOrderItemModel(
                itemId = orderItem.id,
                itemTitle = orderItem.getNormalizedTitle(),
                itemAmounts = orderItem.amounts,
                itemPrice = orderItem.itemPrice,
                itemImageUrl = orderItem.itemImageUrl
            )
        }.also { addAll(it) }

        add(SectionUserDetailCostModel(
            subtotal = orderDetail.subtotalCost,
            deliveryCost = orderDetail.deliveryCost
        ))

        add(SectionUserDetailPaymentModel(
            username = _sectionUserDetailProperty.value?.username!!,
            userPhotoUrl = _sectionUserDetailProperty.value?.userPhotoUrl,
            paymentMethodItem = paymentMethodUtil.getPaymentMethodItem(orderDetail.paymentMethod),
            isPaid = orderDetail.isPaid
        ))
    }
}

@Parcelize
data class SectionUserDetailProperty(
    val orderId: String,
    val username: String,
    val userPhotoUrl: String?
) : Parcelable

sealed class SectionUserDetailUiState {
    object Success : SectionUserDetailUiState()
    data class Error(val message: String?) : SectionUserDetailUiState()
    object Loading : SectionUserDetailUiState()
}