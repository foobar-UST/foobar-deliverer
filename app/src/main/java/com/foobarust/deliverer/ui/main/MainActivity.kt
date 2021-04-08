package com.foobarust.deliverer.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.SensorManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.addRepeatingJob
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import com.foobarust.android.utils.showShortToast
import com.foobarust.deliverer.R
import com.foobarust.deliverer.data.models.GeolocationPoint
import com.foobarust.deliverer.databinding.ActivityMainBinding
import com.foobarust.deliverer.services.LocationServiceComponent
import com.foobarust.deliverer.ui.auth.AuthActivity
import com.foobarust.deliverer.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

private val navGraphIds = listOf(
    R.navigation.navigation_location,
    R.navigation.navigation_settings
)

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),
    LocationServiceComponent.LocationServiceComponentListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationServiceComponent: LocationServiceComponent
    private val viewModel: MainViewModel by viewModels()
    private var currentNavController: LiveData<NavController>? = null

    private val getContentLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val extension = it.getFileExtension(this)
            viewModel.onUploadUserPhoto(uri = it.toString(), extension = extension ?: "")
        }
    }

    @SuppressLint("MissingPermission")
    private val requestLocationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            locationServiceComponent.subscribeLocationUpdates(
                travelMode = viewModel.travelMode.value
            )
            viewModel.onInitializeMap()
        } else {
            showLocationPermissionPermanentDeniedSnackbar()
        }
    }

    @Inject
    lateinit var localBroadcastManager: LocalBroadcastManager

    @Inject
    lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        if (savedInstanceState == null) {
            setupBottomNavigation()
        }

        // Attach location service component to MainActivity
        locationServiceComponent = LocationServiceComponent(
            context = this,
            localBroadcastManager = localBroadcastManager,
            listener = this
        ).also {
            lifecycle.addObserver(it)
        }

        // Get azimuth
        // Note: https://medium.com/androiddevelopers/a-safer-way-to-collect-flows-from-android-uis-23080b1f8bda
        addRepeatingJob(Lifecycle.State.STARTED) {
            sensorManager.orientationFlow(SensorManager.SENSOR_DELAY_UI).collect {
                viewModel.onUpdateCurrentAzimuth(it.azimuth)
            }
        }

        // Terminate location service when the user is signed out
        lifecycleScope.launchWhenStarted {
            viewModel.userSignedOut.collect {
                locationServiceComponent.stopLocationService()
                navigateToAuth()
            }
        }

        // Register get image content launcher (Used for uploading user profile photo)
        lifecycleScope.launchWhenStarted {
            viewModel.getUserPhoto.collect {
                getContentLauncher.launch("image/*")
            }
        }

        // Show toast
        lifecycleScope.launchWhenStarted {
            viewModel.toastMessage.collect {
                showShortToast(it)
            }
        }

        // Travel mode
        lifecycleScope.launchWhenStarted {
            viewModel.travelMode.collect {
                locationServiceComponent.updateTravelMode(it)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    @SuppressLint("MissingPermission")
    override fun onLocationServiceConnected() {
        if (isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            viewModel.onInitializeMap()
            locationServiceComponent.subscribeLocationUpdates(
                travelMode = viewModel.travelMode.value
            )
        } else {
            requestLocationPermission()
        }
    }

    override fun onLocationServiceDisconnected() {
        locationServiceComponent.unsubscribeLocationUpdates()
    }

    override fun onLocationReceived(location: Location) {
        viewModel.onUpdateCurrentLocation(GeolocationPoint(
            latitude = location.latitude,
            longitude = location.longitude
        ))
    }

    private fun setupBottomNavigation() {
        val navController = binding.bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )

        val listener = NavController.OnDestinationChangedListener { controller, _, _ ->
           // TODO: OnDestinationChangedListener
        }

        navController.observe(this) { controller ->
            controller.registerOnDestinationChangedListener(listener)
        }

        currentNavController = navController
    }

    private fun requestLocationPermission() {
        if (isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // If the user denied a previous request, but didn't check "Don't ask again", provide
            // additional rationale.
            showLocationPermissionTemporaryDeniedSnackbar()
        } else {
            startRequestLocationPermission()
        }
    }

    private fun startRequestLocationPermission() {
        requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun showLocationPermissionTemporaryDeniedSnackbar() {
        Snackbar.make(
            binding.coordinatorLayout,
            R.string.request_location_permission_message,
            Snackbar.LENGTH_LONG
        )
            .setAction(android.R.string.ok) {
                startRequestLocationPermission()
            }
            .show()
    }

    private fun showLocationPermissionPermanentDeniedSnackbar() {
        Snackbar.make(
            binding.coordinatorLayout,
            R.string.request_location_permission_message,
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.request_permission_action_settings) {
                startActivity(buildApplicationDetailSettingsIntent())
            }
            .show()
    }

    private fun navigateToAuth() {
        Intent(this, AuthActivity::class.java).apply {
            startActivity(intent)
            finish()
        }
    }
}


