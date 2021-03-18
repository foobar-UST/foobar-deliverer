package com.foobarust.deliverer.ui.order

import androidx.lifecycle.ViewModel
import com.foobarust.deliverer.usecases.auth.GetUserAuthStateUseCase
import com.foobarust.deliverer.usecases.order.GetPendingOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by kevin on 3/8/21
 */

@HiltViewModel
class OrderPendingViewModel @Inject constructor(
    private val getUserAuthStateUseCase: GetUserAuthStateUseCase,
    private val getPendingOrdersUseCase: GetPendingOrdersUseCase
) : ViewModel() {

}