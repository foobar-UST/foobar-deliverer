package com.foobarust.deliverer.services

import android.Manifest
import android.content.*
import android.location.Location
import android.os.IBinder
import androidx.annotation.RequiresPermission
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
 * Created by kevin on 3/11/21
 */

private const val TAG = "LocationService"

class LocationServiceComponent(
    private val context: Context,
    private val localBroadcastManager: LocalBroadcastManager,
    private val onServiceConnected: () -> Unit,
    private val onServiceDisconnected: () -> Unit,
    private val onLocationReceived: (Location) -> Unit
) : LifecycleObserver {
    private var locationService: LocationService? = null

    private var locationServiceBound = false

    private val locationServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.service
            locationServiceBound = true
            onServiceConnected()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            onServiceDisconnected()
            locationService = null
            locationServiceBound = false
        }
    }

    private val locationBroadcastReceiver: LocationBroadcastReceiver by lazy {
        LocationBroadcastReceiver()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        // Bind service
        Intent(context, LocationService::class.java).also {
            context.bindService(it, locationServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        // Unbind foreground location service
        if (locationServiceBound) {
            context.unbindService(locationServiceConnection)
            locationServiceBound = false
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        // Register location broadcast receiver
        localBroadcastManager.registerReceiver(
            locationBroadcastReceiver,
            IntentFilter(LocationService.ACTION_LOCATION_BROADCAST)
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onPause() {
        // Unregister location broadcast receiver
        localBroadcastManager.unregisterReceiver(locationBroadcastReceiver)
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun subscribeLocationUpdates() {
        locationService?.subscribeLocationUpdates()
    }

    fun unsubscribeLocationUpdates() {
        locationService?.unsubscribeLocationUpdates()
    }

    fun stopLocationService() {
        locationService?.stopSelf()
    }

    private inner class LocationBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(LocationService.EXTRA_LOCATION)
            location?.let { onLocationReceived(it) }
        }
    }
}