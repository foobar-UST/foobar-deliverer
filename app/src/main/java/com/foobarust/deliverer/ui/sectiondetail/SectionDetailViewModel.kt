package com.foobarust.deliverer.ui.sectiondetail

import androidx.lifecycle.viewModelScope
import com.foobarust.deliverer.data.models.*
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.states.getSuccessDataOr
import com.foobarust.deliverer.ui.sectiondetail.SectionDetailListModel.*
import com.foobarust.deliverer.ui.sectionuser.SectionUserProperty
import com.foobarust.deliverer.ui.shared.BaseViewModel
import com.foobarust.deliverer.usecases.deliver.ApplySectionDeliveryUseCase
import com.foobarust.deliverer.usecases.deliver.CancelSectionDeliveryUseCase
import com.foobarust.deliverer.usecases.deliver.CompleteSectionDeliveryUseCase
import com.foobarust.deliverer.usecases.maps.GetStaticMapUrlUseCase
import com.foobarust.deliverer.usecases.section.GetSectionDetailUseCase
import com.foobarust.deliverer.usecases.seller.GetSellerDetailUseCase
import com.foobarust.deliverer.utils.cancelIfActive
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 3/19/21
 */

@HiltViewModel
class SectionDetailViewModel @Inject constructor(
    private val getSellerDetailUseCase: GetSellerDetailUseCase,
    private val getSectionDetailUseCase: GetSectionDetailUseCase,
    private val getStaticMapUrlUseCase: GetStaticMapUrlUseCase,
    private val applySectionDeliveryUseCase: ApplySectionDeliveryUseCase,
    private val cancelSectionDeliveryUseCase: CancelSectionDeliveryUseCase,
    private val completeSectionDeliveryUseCase: CompleteSectionDeliveryUseCase
) : BaseViewModel() {

    private val _sellerDetail = MutableStateFlow<SellerDetail?>(null)

    private val _sectionDetail = MutableStateFlow<SellerSectionDetail?>(null)
    val sectionDetail: StateFlow<SellerSectionDetail?> = _sectionDetail.asStateFlow()

    private val _staticMapUrl = MutableStateFlow<String?>(null)

    // Hide start delivery button in transit mode
    private val _sectionDetailMode = MutableStateFlow<SectionDetailMode?>(SectionDetailMode.OVERVIEW)

    private val _sectionDetailListModels = MutableStateFlow<List<SectionDetailListModel>>(emptyList())
    val sectionDetailListModels: StateFlow<List<SectionDetailListModel>> = _sectionDetailListModels
        .asStateFlow()

    private val _loadingUiState = MutableStateFlow<SectionDetailUiState>(SectionDetailUiState.Loading)
    val loadingUiState: StateFlow<SectionDetailUiState> = _loadingUiState.asStateFlow()

    private val _updateUiState = MutableStateFlow<SectionDetailUiState?>(null)
    val updateUiState: StateFlow<SectionDetailUiState?> = _updateUiState.asStateFlow()

    private val _showApplyConfirmDialog = Channel<SellerSectionDetail>()
    val showApplyConfirmDialog: Flow<SellerSectionDetail> = _showApplyConfirmDialog.receiveAsFlow()

    private val _navigateToSectionUser = Channel<SectionUserProperty>()
    val navigateToSectionUser: Flow<SectionUserProperty> = _navigateToSectionUser.receiveAsFlow()

    private val _finishSwipeRefresh = Channel<Unit>()
    val finishSwipeRefresh: Flow<Unit> = _finishSwipeRefresh.receiveAsFlow()

    private var fetchSectionDetailJob: Job? = null

    init {
        // Build section detail list
        viewModelScope.launch {
            combine(
                _sellerDetail.filterNotNull(),
                _sectionDetail.filterNotNull(),
                _staticMapUrl.filterNotNull(),
                _sectionDetailMode.filterNotNull(),
                ::buildSectionDetailListModels
            ).collect()
        }
    }

    fun onFetchSectionDetail(
        sectionId: String, mode: SectionDetailMode, isSwipeRefresh: Boolean = false
    ) {
        _sectionDetailMode.value = mode

        fetchSectionDetailJob.cancelIfActive()
        fetchSectionDetailJob = viewModelScope.launch {
            // Fetch section detail
            getSectionDetailUseCase(sectionId).onEach {
                when (it) {
                    is Resource.Success -> {
                        _sectionDetail.value = it.data
                        _loadingUiState.value = SectionDetailUiState.Success
                        _finishSwipeRefresh.offer(Unit)
                    }
                    is Resource.Error -> {
                        _loadingUiState.value = SectionDetailUiState.Error(it.message)
                        _finishSwipeRefresh.offer(Unit)
                    }
                    is Resource.Loading -> if (!isSwipeRefresh) {
                        _loadingUiState.value = SectionDetailUiState.Loading
                    }
                }
            }.launchIn(this)

            // Fetch seller detail
            _sectionDetail.filterNotNull().flatMapLatest {
                getSellerDetailUseCase(it.sellerId)
            }.onEach {
                _sellerDetail.value = it.getSuccessDataOr(null)
            }.launchIn(this)

            // Fetch static map
            _sectionDetail.filterNotNull().flatMapLatest {
                getStaticMapUrlUseCase(it.deliveryLocation.locationPoint)
            }.onEach {
                _staticMapUrl.value = it.getSuccessDataOr(null)
            }.launchIn(this)
        }
    }

    fun onConfirmApplySectionDelivery() {
        _sectionDetail.value?.let {
            _showApplyConfirmDialog.offer(it)
        } ?: showToastMessage("Section is null.")
    }

    fun onApplySectionDelivery(sectionId: String) = viewModelScope.launch {
        applySectionDeliveryUseCase(sectionId).collect {
            _updateUiState.value = when (it) {
                is Resource.Success -> SectionDetailUiState.Success
                is Resource.Error -> SectionDetailUiState.Error(it.message)
                is Resource.Loading -> SectionDetailUiState.Loading
            }
        }
    }

    fun onCancelSectionDelivery() = viewModelScope.launch {
        _sectionDetail.value?.let { sectionDetail ->
            cancelSectionDeliveryUseCase(sectionDetail.id).collect {
                _updateUiState.value = when (it) {
                    is Resource.Success -> SectionDetailUiState.Success
                    is Resource.Error -> SectionDetailUiState.Error(it.message)
                    is Resource.Loading -> SectionDetailUiState.Loading
                }
            }
        }
    }

    fun onCompleteSectionDelivery() = viewModelScope.launch {
        _sectionDetail.value?.let { sectionDetail ->
            completeSectionDeliveryUseCase(sectionDetail.id).collect {
                _updateUiState.value = when (it) {
                    is Resource.Success -> SectionDetailUiState.Success
                    is Resource.Error -> SectionDetailUiState.Error(it.message)
                    is Resource.Loading -> SectionDetailUiState.Loading
                }
            }
        }
    }

    fun onNavigateToSectionUser() {
        _sectionDetail.value?.let { sectionDetail ->
            _navigateToSectionUser.offer(SectionUserProperty(
                sectionId = sectionDetail.id,
                joinedUsersCount = sectionDetail.joinedUsersCount,
                maxUsersCount = sectionDetail.maxUsers
            ))
        }
    }

    private fun buildSectionDetailListModels(
        sellerDetail: SellerDetail,
        sectionDetail: SellerSectionDetail,
        staticMapUrl: String,
        sectionDetailMode: SectionDetailMode
    ) {
        _sectionDetailListModels.value = buildList {
            add(SectionDetailTimeModel(
                deliveryTime = sectionDetail.deliveryTime
            ))

            add(SectionDetailInfoModel(
                sectionId = sectionDetail.id,
                sectionTitle = sectionDetail.getNormalizedTitle(),
                sectionDescription = sectionDetail.getNormalizedDescription(),
                sectionDeliveryTime = sectionDetail.deliveryTime,
                sectionMaxUsers = sectionDetail.maxUsers,
                sectionJoinedUsersCount = sectionDetail.joinedUsersCount
            ))

            if (sectionDetailMode == SectionDetailMode.TRANSIT) {
                add(SectionDetailOrdersModel)
            }

            add(SectionDetailSellerModel(
                sellerId = sellerDetail.id,
                sellerName = sellerDetail.getNormalizedName(),
                sellerImageUrl = sellerDetail.imageUrl,
                sellerPhoneNum = sellerDetail.phoneNum
            ))

            add(SectionDetailLocationModel(
                deliveryLocation = sectionDetail.deliveryLocation,
                staticMapImageUrl = staticMapUrl
            ))

            // Add complete button during pick up state
            if (sectionDetailMode == SectionDetailMode.TRANSIT &&
                sectionDetail.state == SellerSectionState.READY_FOR_PICK_UP) {
                add(SectionDetailCompleteModel)
            }

            // Add cancel button after the section is accepted for delivery
            if (sectionDetailMode == SectionDetailMode.TRANSIT) {
                add(SectionDetailCancelModel)
            }
        }
    }
}

sealed class SectionDetailUiState {
    object Success : SectionDetailUiState()
    data class Error(val message: String?) : SectionDetailUiState()
    object Loading : SectionDetailUiState()
}

enum class SectionDetailMode {
    OVERVIEW,
    TRANSIT
}