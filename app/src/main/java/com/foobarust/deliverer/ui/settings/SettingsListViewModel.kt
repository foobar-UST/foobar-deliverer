package com.foobarust.deliverer.ui.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

/**
 * Created by kevin on 3/18/21
 */

@HiltViewModel
class SettingsViewModel @Inject constructor(): ViewModel() {

    private val _backPressed = Channel<Unit>()
    val backPressed: Flow<Unit> = _backPressed.receiveAsFlow()

    private val _snackBarMessage = Channel<String>()
    val snackBarMessage: Flow<String> = _snackBarMessage.receiveAsFlow()

    fun onBackPressed() {
        _backPressed.offer(Unit)
    }

    fun onShowSnackBarMessage(message: String) {
        _snackBarMessage.offer(message)
    }
}