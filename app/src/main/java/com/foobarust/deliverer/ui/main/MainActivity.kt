package com.foobarust.deliverer.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.foobarust.deliverer.BuildConfig
import com.foobarust.deliverer.R
import com.foobarust.deliverer.databinding.ActivityMainBinding
import com.foobarust.deliverer.services.LocationServiceComponent
import com.foobarust.deliverer.ui.auth.AuthActivity
import com.foobarust.deliverer.ui.settings.SettingsFragment
import com.foobarust.deliverer.utils.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.ktx.awaitMap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

private const val TAG = "MainActivity"
private const val LOCATION_PERMISSION_REQUEST_CODE = 34

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationServiceComponent: LocationServiceComponent
    private lateinit var getContentLauncher: ActivityResultLauncher<String>
    private val viewModel: MainViewModel by viewModels()
    private var googleMap: GoogleMap? = null

    @Inject
    lateinit var localBroadcastManager: LocalBroadcastManager
    @Inject
    lateinit var sensorManager: SensorManager

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        initializeMapFragment()

        // Settings button
        with(binding.settingsButton) {
            applySystemWindowInsetsMargin(applyTop = true)
            setOnClickListener { navigateToSettings() }
        }

        // Fixed camera position button
        binding.fixedCameraPositionSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onUpdateFixedCameraPosition(isFixed = isChecked)
        }

        // Register content launcher
        getContentLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val extension = it.getFileExtension(this)
                viewModel.onUploadUserPhoto(uri = it.toString(), extension = extension ?: "")
            }
        }

        // Attach location service component to MainActivity
        locationServiceComponent = LocationServiceComponent(
            context = this,
            localBroadcastManager = localBroadcastManager,
            onServiceConnected = {
                if (isLocationPermissionApproved()) {
                    locationServiceComponent.subscribeLocationUpdates()
                } else {
                    requestLocationPermission()
                }
            },
            onServiceDisconnected = {
                locationServiceComponent.unsubscribeLocationUpdates()
            },
            onLocationReceived = {
                viewModel.onUpdateCurrentLocation(it)
            }
        ).also {
            lifecycle.addObserver(it)
        }

        // Orientation sensor
        lifecycleScope.launchWhenResumed {
            sensorManager.orientationFlow(SensorManager.SENSOR_DELAY_UI).collect {
                viewModel.onUpdateCurrentAzimuth(it.azimuth)
            }
        }

        // Terminate location service when the user is signed out
        lifecycleScope.launchWhenCreated {
            viewModel.userSignedOut.collect {
                locationServiceComponent.stopLocationService()
                navigateToAuth()
            }
        }

        // Register get image content launcher (Used for uploading user profile photo)
        lifecycleScope.launchWhenCreated {
            viewModel.getUserPhoto.collect {
                getContentLauncher.launch("image/*")
            }
        }

        // Show snack bar
        lifecycleScope.launchWhenStarted {
            viewModel.snackBarMessage.collect {
                showMessageSnackbar(it)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> when {
                grantResults.isEmpty() -> {
                    // If user interaction was interrupted, the permission request is cancelled and
                    // received empty arrays.
                }
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    // Permissions was granted.
                    locationServiceComponent.subscribeLocationUpdates()
                }
                else -> {
                    // Permission denied.
                    showPermissionPermanentDeniedSnackbar()
                }
            }
        }
    }

    private fun initializeMapFragment() {
        lifecycleScope.launchWhenCreated {
            val mapFragment = supportFragmentManager.findFragmentById(R.id.map_container_view)
                as SupportMapFragment

            googleMap = mapFragment.awaitMap().apply {
                if (isNightModeOn()) {
                    val nightStyle = MapStyleOptions.loadRawResourceStyle(
                        this@MainActivity, R.raw.night_map_style
                    )
                    setMapStyle(nightStyle)
                }
            }
        }

        // Set camera position
        lifecycleScope.launchWhenStarted {
            viewModel.currentCameraPosition.collect { position ->
                Log.d(TAG, position.toString())
                googleMap?.setCameraPosition(
                    bearing = position.azimuth,
                    center = position.latLng
                )
            }
        }
    }

    private fun isLocationPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun requestLocationPermission() {
        if (isLocationPermissionApproved()) {
            // If the user denied a previous request, but didn't check "Don't ask again", provide
            // additional rationale.
            showPermissionTemporaryDeniedSnackbar()
        } else {
            startRequestLocationPermission()
        }
    }

    private fun startRequestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun showMessageSnackbar(message: String) {
        Snackbar.make(binding.coordinatorLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showPermissionTemporaryDeniedSnackbar() {
        Snackbar.make(
            binding.coordinatorLayout,
            R.string.request_permission_message,
            Snackbar.LENGTH_LONG
        )
            .setAction(android.R.string.ok) {
                // Request permission
                startRequestLocationPermission()
            }
            .show()
    }

    private fun showPermissionPermanentDeniedSnackbar() {
        Snackbar.make(
            binding.coordinatorLayout,
            R.string.request_permission_message,
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.request_permission_action_settings) {
                Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts(
                        "package",
                        BuildConfig.APPLICATION_ID,
                        null
                    )
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }.also {
                    startActivity(it)
                }
            }
            .show()
    }

    private fun navigateToAuth() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToSettings() {
        SettingsFragment.newInstance().show(supportFragmentManager, SettingsFragment.TAG)
    }
}


