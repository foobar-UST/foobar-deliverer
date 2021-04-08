package com.foobarust.deliverer.ui.verify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.usecases.deliver.VerifyOrderDeliveredParameters
import com.foobarust.deliverer.usecases.deliver.VerifyOrderDeliveredUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 4/2/21
 */

@HiltViewModel
class VerifyDeliveryViewModel @Inject constructor(
    private val verifyOrderDeliveredUseCase: VerifyOrderDeliveredUseCase
) : ViewModel() {

    private val _verifyDeliveryUiState = MutableStateFlow<VerifyDeliveryUiState>(
        VerifyDeliveryUiState.Capturing
    )
    val verifyDeliveryUiState: StateFlow<VerifyDeliveryUiState> = _verifyDeliveryUiState
        .asStateFlow()

    private val _toastMessage = Channel<String?>()
    val toastMessage: Flow<String?> = _toastMessage.receiveAsFlow()

    fun onReceiveCaptureResult(
        orderId: String,
        verifyCode: String,
        content: String
    ) = viewModelScope.launch {
        val params = VerifyOrderDeliveredParameters(orderId, verifyCode, content)
        verifyOrderDeliveredUseCase(params).collectLatest {
            when (it) {
                is Resource.Success -> {
                    _verifyDeliveryUiState.value = VerifyDeliveryUiState.VerifyCompleted
                }
                is Resource.Error -> {
                    _verifyDeliveryUiState.value = VerifyDeliveryUiState.Capturing
                    _toastMessage.offer(it.message)
                }
                is Resource.Loading -> {
                    _verifyDeliveryUiState.value = VerifyDeliveryUiState.Processing(verifyCode)
                }
            }
        }
    }
}

sealed class VerifyDeliveryUiState {
    data class Processing(val verifyCode: String) : VerifyDeliveryUiState()
    object Capturing : VerifyDeliveryUiState()
    object VerifyCompleted : VerifyDeliveryUiState()
}