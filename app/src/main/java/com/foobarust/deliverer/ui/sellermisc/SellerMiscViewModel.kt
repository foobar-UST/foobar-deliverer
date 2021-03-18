package com.foobarust.deliverer.ui.sellermisc

import androidx.lifecycle.viewModelScope
import com.foobarust.deliverer.data.models.GeolocationPoint
import com.foobarust.deliverer.data.models.SellerDetail
import com.foobarust.deliverer.data.models.SellerType
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.ui.shared.BaseViewModel
import com.foobarust.deliverer.usecases.AuthState
import com.foobarust.deliverer.usecases.auth.GetUserAuthStateUseCase
import com.foobarust.deliverer.usecases.maps.GetDirectionsParameters
import com.foobarust.deliverer.usecases.maps.GetDirectionsUseCase
import com.foobarust.deliverer.usecases.seller.GetSellerDetailUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 10/11/20
 */

@HiltViewModel
class SellerMiscViewModel @Inject constructor(
    private val getUserAuthStateUseCase: GetUserAuthStateUseCase,
    private val getSellerDetailUseCase: GetSellerDetailUseCase,
    private val getDirectionsUseCase: GetDirectionsUseCase
) : BaseViewModel() {

    private val _sellerLocation = MutableStateFlow<LatLng?>(null)
    val sellerLocation: StateFlow<LatLng?> = _sellerLocation.asStateFlow()

    private val _offCampusDeliveryRoute = MutableStateFlow<List<LatLng>>(emptyList())
    val offCampusDeliveryRoute: StateFlow<List<LatLng>> = _offCampusDeliveryRoute.asStateFlow()

    private val _sellerMiscUiState = MutableStateFlow<SellerMiscUiState>(SellerMiscUiState.Loading)
    val sellerMiscUiState: StateFlow<SellerMiscUiState> = _sellerMiscUiState.asStateFlow()

    fun onFetchSellerDetail() = viewModelScope.launch {
        getUserAuthStateUseCase(Unit).flatMapLatest {
            if (it is AuthState.Authenticated && it.data.employedBy != null) {
                getSellerDetailUseCase(it.data.employedBy)
            } else {
                emptyFlow()
            }
        }.collect {
            when (it) {
                is Resource.Success -> {
                    val sellerDetail = it.data
                    _sellerLocation.value = LatLng(
                        sellerDetail.location.locationPoint.latitude,
                        sellerDetail.location.locationPoint.longitude
                    )
                    _sellerMiscUiState.value = SellerMiscUiState.Success(it.data)

                    if (sellerDetail.type == SellerType.OFF_CAMPUS) {
                        buildDeliveryRoute(sellerDetail.location.locationPoint)
                    }
                }
                is Resource.Error -> {
                    _sellerMiscUiState.value = SellerMiscUiState.Error(it.message)
                }
                is Resource.Loading -> {
                    _sellerMiscUiState.value = SellerMiscUiState.Loading
                }
            }
        }
    }

    private fun buildDeliveryRoute(locationPoint: GeolocationPoint) = viewModelScope.launch {
        getDirectionsUseCase(
            GetDirectionsParameters(
                latitude = locationPoint.latitude,
                longitude = locationPoint.longitude
            )

        ).collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    val locationPoints = resource.data
                    _offCampusDeliveryRoute.value = locationPoints.map {
                        LatLng(it.latitude, it.longitude)
                    }
                }
                is Resource.Error -> showToastMessage(resource.message)
                is Resource.Loading -> Unit
            }
        }
    }
}

sealed class SellerMiscUiState {
    data class Success(val sellerDetail: SellerDetail) : SellerMiscUiState()
    data class Error(val message: String?) : SellerMiscUiState()
    object Loading : SellerMiscUiState()
}
