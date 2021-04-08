package com.foobarust.deliverer.ui.bottomdrawer

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.foobarust.deliverer.R
import com.foobarust.deliverer.data.models.SellerSectionDetail
import com.foobarust.deliverer.data.models.getDeliveryTimeString
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.ui.shared.BaseViewModel
import com.foobarust.deliverer.usecases.AuthState
import com.foobarust.deliverer.usecases.auth.GetUserAuthStateUseCase
import com.foobarust.deliverer.usecases.section.GetDeliveryTimeLeftUseCase
import com.foobarust.deliverer.usecases.section.GetSectionDetailUseCase
import com.foobarust.deliverer.usecases.section.PeriodicTimerUseCase
import com.foobarust.deliverer.utils.cancelIfActive
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by kevin on 3/21/21
 */

@HiltViewModel
class SectionTransitViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getUserAuthStateUseCase: GetUserAuthStateUseCase,
    private val getSectionDetailUseCase: GetSectionDetailUseCase,
    periodicTimerUseCase: PeriodicTimerUseCase,
    private val getDeliveryTimeLeftUseCase: GetDeliveryTimeLeftUseCase
) : BaseViewModel() {

    private val _sectionDetail = MutableStateFlow<SellerSectionDetail?>(null)
    val sectionDetail: StateFlow<SellerSectionDetail?> = _sectionDetail.asStateFlow()

    private val _sectionTransitUiState = MutableStateFlow<SectionTransitUiState>(
        SectionTransitUiState.Loading
    )
    val sectionTransitUiState: StateFlow<SectionTransitUiState> = _sectionTransitUiState
        .asStateFlow()

    // Arg: section id
    private val _navigateToSectionDetail = Channel<String>()
    val navigateToSectionDetail: Flow<String> = _navigateToSectionDetail.receiveAsFlow()

    private var fetchSectionTransitJob: Job? = null

    val deliveryTimeRemainTitle: StateFlow<String> = periodicTimerUseCase(
        TimeUnit.MINUTES.toMillis(1)
    )
        .combine(_sectionDetail.filterNotNull()) { _, sectionDetail ->
            val timeLeftStr = getDeliveryTimeLeftUseCase(
                sectionDetail = sectionDetail,
                hoursSuffix = context.getString(R.string.section_transit_delivery_time_left_hours),
                minutesSuffix = context.getString(R.string.section_transit_delivery_time_left_minutes)
            )
            context.getString(
                R.string.section_transit_delivery_time_left,
                timeLeftStr,
                sectionDetail.getDeliveryTimeString()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ""
        )

    val deliveryAddress: Flow<String?> = _sectionDetail
        .filterNotNull()
        .map { sectionDetail ->
            if (Locale.getDefault().language == Locale("zh").language) {
                sectionDetail.deliveryLocation.addressZh
            } else {
                sectionDetail.deliveryLocation.address
            }
        }

    init {
        onFetchSectionTransit()
    }

    fun onFetchSectionTransit() {
        fetchSectionTransitJob?.cancelIfActive()
        fetchSectionTransitJob = viewModelScope.launch {
            getUserAuthStateUseCase(Unit).flatMapLatest { authState ->
                if (authState is AuthState.Authenticated &&
                    authState.data.sectionInDelivery != null) {
                    getSectionDetailUseCase(authState.data.sectionInDelivery)
                } else {
                    _sectionDetail.value = null
                    emptyFlow()
                }
            }.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        _sectionDetail.value = it.data
                        _sectionTransitUiState.value = SectionTransitUiState.Success
                    }
                    is Resource.Error -> {
                        _sectionTransitUiState.value = SectionTransitUiState.Error(it.message)
                    }
                    is Resource.Loading -> {
                        _sectionTransitUiState.value = SectionTransitUiState.Loading
                    }
                }
            }
        }
    }

    fun onNavigateToSectionDetail() {
        _sectionDetail.value?.let {
            _navigateToSectionDetail.offer(it.id)
        }
    }
}

sealed class SectionTransitUiState {
    object Success : SectionTransitUiState()
    data class Error(val message: String?) : SectionTransitUiState()
    object Loading : SectionTransitUiState()
}