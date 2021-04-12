package com.foobarust.deliverer.ui.main

import android.app.NotificationManager
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.foobarust.deliverer.R
import com.foobarust.deliverer.data.models.GeolocationPoint
import com.foobarust.deliverer.data.models.SellerSectionDetail
import com.foobarust.deliverer.data.models.SellerSectionState
import com.foobarust.deliverer.data.models.TravelMode
import com.foobarust.deliverer.di.ApplicationScope
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.ui.shared.AppConfig.ARRIVE_DESTINATION_RANGE_METER
import com.foobarust.deliverer.ui.shared.AppConfig.CENTER_CAMERA_INTERVAL
import com.foobarust.deliverer.ui.shared.AppConfig.GET_DIRECTIONS_DRIVING_INTERVAL
import com.foobarust.deliverer.ui.shared.AppConfig.GET_DIRECTIONS_RANGE_METER
import com.foobarust.deliverer.ui.shared.AppConfig.GET_DIRECTIONS_WALKING_INTERVAL
import com.foobarust.deliverer.ui.shared.BaseViewModel
import com.foobarust.deliverer.usecases.AuthState
import com.foobarust.deliverer.usecases.auth.GetUserAuthStateUseCase
import com.foobarust.deliverer.usecases.auth.SignOutUseCase
import com.foobarust.deliverer.usecases.deliver.StartSectionPickupUseCase
import com.foobarust.deliverer.usecases.maps.GetDirectionsParameters
import com.foobarust.deliverer.usecases.maps.GetDirectionsUseCase
import com.foobarust.deliverer.usecases.maps.GetDistanceBetweenUseCase
import com.foobarust.deliverer.usecases.maps.GetElapsedDistanceUseCase
import com.foobarust.deliverer.usecases.user.UploadUserPhotoParameters
import com.foobarust.deliverer.usecases.user.UploadUserPhotoUseCase
import com.foobarust.deliverer.utils.ProgressNotification
import com.foobarust.deliverer.utils.buildProgressNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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
    private val signOutUseCase: SignOutUseCase,
    private val getDirectionsUseCase: GetDirectionsUseCase,
    private val getElapsedDistanceUseCase: GetElapsedDistanceUseCase,
    private val getDistanceBetweenUseCase: GetDistanceBetweenUseCase,
    private val startSectionPickupUseCase: StartSectionPickupUseCase
) : BaseViewModel() {

    private val _currentLocation = MutableStateFlow<GeolocationPoint?>(null)

    private val _currentAzimuth = MutableStateFlow(0f)

    private val _sectionDetail = MutableStateFlow<SellerSectionDetail?>(null)

    private val _travelMode = MutableStateFlow(TravelMode.DRIVING)
    val travelMode: StateFlow<TravelMode> = _travelMode.asStateFlow()

    private val _deliveryRoute = MutableStateFlow<List<GeolocationPoint>>(emptyList())
    val deliveryRoute: StateFlow<List<GeolocationPoint>> = _deliveryRoute.asStateFlow()

    // Arg: section id
    private val _sectionInDelivery = MutableStateFlow<String?>(null)
    val sectionInDelivery: StateFlow<String?> = _sectionInDelivery.asStateFlow()

    private val _initMap = Channel<Unit>()
    val initMap: Flow<Unit> = _initMap.receiveAsFlow()

    private val _userSignedOut = Channel<Unit>()
    val userSignedOut: Flow<Unit> = _userSignedOut.receiveAsFlow()

    private val _getUserPhoto = Channel<Unit>()
    val getUserPhoto: Flow<Unit> = _getUserPhoto.receiveAsFlow()

    val currentLocationInfo: SharedFlow<LocationInfo> = _currentLocation.filterNotNull()
        .combine(_currentAzimuth) { location, azimuth ->
            LocationInfo(
                locationPoint = location,
                azimuth = azimuth
            )
        }
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1
        )

    val centerCameraLocation: Flow<GeolocationPoint?> = _sectionInDelivery
        .combine(_currentLocation) { sectionId, currentLocation ->
            if (sectionId != null) currentLocation else null
        }
        .onEach { delay(CENTER_CAMERA_INTERVAL)  }

    val showArrivedDest: StateFlow<Boolean> = _currentLocation
        .filterNotNull()
        .combine(_sectionDetail.filterNotNull()) { currentLocation, sectionDetail ->
            val hasArrived = getDistanceBetweenUseCase(
                currentLocation = currentLocation,
                destination = sectionDetail.deliveryLocation.locationPoint
            ) <= ARRIVE_DESTINATION_RANGE_METER

            sectionDetail.state == SellerSectionState.SHIPPED && hasArrived
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    init {
        // Observe authentication state
        viewModelScope.launch {
            getUserAuthStateUseCase(Unit).collect { authState ->
                when (authState) {
                    is AuthState.Authenticated -> {
                        val sectionInDelivery = authState.data.sectionInDelivery
                        _sectionInDelivery.value = sectionInDelivery

                        // Clear delivery route if there is no ongoing delivery
                        if (sectionInDelivery == null) {
                            _deliveryRoute.value = emptyList()
                        }
                    }
                    AuthState.Unauthenticated, AuthState.Unverified -> {
                        _sectionInDelivery.value = null
                        _deliveryRoute.value = emptyList()
                        _userSignedOut.offer(Unit)
                    }
                    AuthState.Loading -> Unit
                }
            }
        }

        // Fetch new delivery route when:
        // 1. travelled for certain distance
        // 2. travel mode changed
        // 3. delivery location changed
        viewModelScope.launch {
            _sectionInDelivery.flatMapLatest { sectionId ->
                // Get route only if there is an ongoing delivery
                if (sectionId != null) _travelMode else emptyFlow()
            }.flatMapLatest { mode ->
                val updateInterval = when (mode) {
                    TravelMode.DRIVING -> GET_DIRECTIONS_DRIVING_INTERVAL
                    TravelMode.WALKING -> GET_DIRECTIONS_WALKING_INTERVAL
                }

                val deliveryLocationFlow = _sectionDetail.filterNotNull()
                    .map { it.deliveryLocation.locationPoint }

                combine(
                    throttledCurrentLocationFlow(updateInterval),
                    deliveryLocationFlow,
                    _travelMode
                ) { currentLocation, deliveryLocation, deliveryMode ->
                    GetDirectionsParameters(
                        currentLocation = currentLocation,
                        destination = deliveryLocation,
                        travelMode = deliveryMode
                    )
                }
            }.flatMapLatest {
                showToastMessage("fetch directions")
                getDirectionsUseCase(it)
            }.map {
                when (it) {
                    is Resource.Success -> it.data
                    is Resource.Error -> {
                        showToastMessage(it.message)
                        emptyList()
                    }
                    is Resource.Loading -> emptyList()
                }
            }.collectLatest { route ->
                _deliveryRoute.value = route
            }
        }
    }

    fun onInitializeMap() {
        _initMap.offer(Unit)
    }

    fun onUpdateCurrentLocation(locationPoint: GeolocationPoint) {
        _currentLocation.value = locationPoint
    }

    fun onUpdateCurrentAzimuth(azimuth: Float) {
        _currentAzimuth.value = azimuth
    }

    fun onUpdateSectionDetail(sectionDetail: SellerSectionDetail?) {
        _sectionDetail.value = sectionDetail
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

    fun onUpdateTravelMode(travelMode: TravelMode) {
        _travelMode.value = travelMode
    }

    fun onStartSectionPickup() = viewModelScope.launch {
        val sectionId = _sectionInDelivery.value ?: return@launch
        startSectionPickupUseCase(sectionId).collect {
            when (it) {
                is Resource.Success -> Unit
                is Resource.Error -> showToastMessage(it.message)
                is Resource.Loading -> Unit
            }
        }
    }

    private fun throttledCurrentLocationFlow(throttleMills: Long): Flow<GeolocationPoint> = flow {
        val currentLocationFlow = _currentLocation.filterNotNull()
        // Emit the first result immediately,
        // after that, throttle each result by throttleMills
        emit(currentLocationFlow.first())

        val throttledFlow = currentLocationFlow.onEach {
            delay(throttleMills)
        }.filter {
            // Emit location only when reaching required travel distance
            val travelledDistance = getElapsedDistanceUseCase(it)
            travelledDistance != null && travelledDistance > GET_DIRECTIONS_RANGE_METER
        }

        emitAll(throttledFlow)
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

data class LocationInfo(
    val locationPoint: GeolocationPoint,
    val azimuth: Float
)
