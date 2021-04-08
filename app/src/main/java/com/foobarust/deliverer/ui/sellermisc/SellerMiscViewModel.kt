package com.foobarust.deliverer.ui.sellermisc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.deliverer.data.models.SellerDetail
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.usecases.AuthState
import com.foobarust.deliverer.usecases.auth.GetUserAuthStateUseCase
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
    private val getSellerDetailUseCase: GetSellerDetailUseCase
) : ViewModel() {

    private val _sellerLocation = MutableStateFlow<LatLng?>(null)
    val sellerLocation: StateFlow<LatLng?> = _sellerLocation.asStateFlow()

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
}

sealed class SellerMiscUiState {
    data class Success(val sellerDetail: SellerDetail) : SellerMiscUiState()
    data class Error(val message: String?) : SellerMiscUiState()
    object Loading : SellerMiscUiState()
}
