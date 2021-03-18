package com.foobarust.deliverer.ui.main

import android.app.NotificationManager
import android.content.Context
import android.location.Location
import androidx.lifecycle.viewModelScope
import com.foobarust.deliverer.R
import com.foobarust.deliverer.di.ApplicationScope
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.ui.shared.BaseViewModel
import com.foobarust.deliverer.usecases.AuthState
import com.foobarust.deliverer.usecases.auth.GetUserAuthStateUseCase
import com.foobarust.deliverer.usecases.auth.SignOutUseCase
import com.foobarust.deliverer.usecases.user.UploadUserPhotoParameters
import com.foobarust.deliverer.usecases.user.UploadUserPhotoUseCase
import com.foobarust.deliverer.utils.ProgressNotification
import com.foobarust.deliverer.utils.buildProgressNotification
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 2/17/21
 */

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationScope private val externalScope: CoroutineScope,
    private val notificationManager: NotificationManager,
    private val getUserAuthStateUseCase: GetUserAuthStateUseCase,
    private val uploadUserPhotoUseCase: UploadUserPhotoUseCase,
    private val signOutUseCase: SignOutUseCase
) : BaseViewModel() {

    private val _fixedCameraPosition = MutableStateFlow(false)

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    private val _currentAzimuth = MutableStateFlow(0f)

    val currentCameraPosition: Flow<MapCameraPosition> = _fixedCameraPosition
        .flatMapLatest { fixed ->
            if (fixed) {
                // Use both location and azimuth
                _currentLocation.filterNotNull()
                    .combine(_currentAzimuth) { location, azimuth ->
                        MapCameraPosition(location, azimuth)
                    }
            } else {
                // Use location only
                _currentLocation.filterNotNull()
                    .map { MapCameraPosition(it) }
            }
        }

    private val _userSignedOut = Channel<Unit>()
    val userSignedOut: Flow<Unit> = _userSignedOut.receiveAsFlow()

    private val _getUserPhoto = Channel<Unit>()
    val getUserPhoto: Flow<Unit> = _getUserPhoto.receiveAsFlow()

    private val _snackBarMessage = Channel<String>()
    val snackBarMessage: Flow<String> = _snackBarMessage.receiveAsFlow()

    init {
        // Observe auth state
        viewModelScope.launch {
            getUserAuthStateUseCase(Unit).collect {
                when (it) {
                    AuthState.Unauthenticated, AuthState.Unverified -> _userSignedOut.offer(Unit)
                    else -> Unit
                }
            }
        }
    }

    fun onUpdateFixedCameraPosition(isFixed: Boolean) {
        _fixedCameraPosition.value = isFixed
    }

    fun onUpdateCurrentLocation(location: Location) {
        _currentLocation.value = LatLng(location.latitude, location.longitude)
    }

    fun onUpdateCurrentAzimuth(azimuth: Float) {
        _currentAzimuth.value = azimuth
    }

    fun onUploadUserPhoto(uri: String, extension: String) = externalScope.launch {
        val progressNotification = notificationManager.buildProgressNotification(
            context = context,
            channelId = context.getString(R.string.notification_channel_upload_id),
            title = context.getString(R.string.notification_upload_user_photo_title),
            messageBody = context.getString(R.string.notification_upload_user_photo_body)
        )

        val params = UploadUserPhotoParameters(
            photoUri = uri,
            photoExtension = extension
        )

        uploadUserPhotoUseCase(params).collect {
            when (it) {
                is Resource.Success -> {
                    clearProgressNotification(progressNotification)
                }
                is Resource.Error -> {
                    clearProgressNotification(progressNotification)
                    showToastMessage(it.message)
                }
                is Resource.Loading -> {
                    showProgressNotification(progressNotification, it.progress)
                }
            }
        }
    }

    fun onPickUserPhoto() {
        _getUserPhoto.offer(Unit)
    }

    fun onUserSignOut() = viewModelScope.launch {
        signOutUseCase(Unit).collect {
            if (it is Resource.Error) {
                showToastMessage(it.message)
            }
        }
    }

    fun onShowSnackBarMessage(message: String) {
        _snackBarMessage.offer(message)
    }

    private fun showProgressNotification(progressNotification: ProgressNotification, progress: Double?) {
        progressNotification.updateProgress(progress ?: 0.0)
        notificationManager.notify(
            progressNotification.notificationId,
            progressNotification.getNotification()
        )
    }

    private fun clearProgressNotification(progressNotification: ProgressNotification) {
        notificationManager.cancel(progressNotification.notificationId)
    }
}

data class MapCameraPosition(
    val latLng: LatLng,
    val azimuth: Float? = null
)

sealed class MapCameraMode {
    data class Free(val latLng: LatLng) : MapCameraMode()
    data class Fixed(val latLng: LatLng, val azimuth: Float) : MapCameraMode()
}