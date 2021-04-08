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
import com.foobarust.deliverer.data.models.TravelMode

/**
 * Created by kevin on 3/11/21
 */

class LocationServiceComponent(
    private val context: Context,
    private val localBroadcastManager: LocalBroadcastManager,
    private val listener: LocationServiceComponentListener
) : LifecycleObserver {
    private var locationService: LocationService? = null

    private var locationServiceBound = false

    private val locationBroadcastReceiver: LocationBroadcastReceiver by lazy {
        LocationBroadcastReceiver()
    }

    private val locationServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.service
            locationServiceBound = true
            listener.onLocationServiceConnected()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            locationService = null
            locationServiceBound = false
            listener.onLocationServiceDisconnected()
        }
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
    fun subscribeLocationUpdates(travelMode: TravelMode? = null) {
        locationService?.subscribeLocationUpdates(travelMode)
    }

    fun unsubscribeLocationUpdates() {
        locationService?.unsubscribeLocationUpdates()
    }

    fun updateTravelMode(travelMode: TravelMode) {
        locationService?.updateTravelMode(travelMode)
    }

    fun stopLocationService() {
        locationService?.stopSelf()
    }

    private inner class LocationBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.getParcelableExtra<Location>(
                LocationService.EXTRA_LOCATION_BROADCAST_LOCATION
            )?.let {
                listener.onLocationReceived(it)
            }
        }
    }

    interface LocationServiceComponentListener {
        fun onLocationServiceConnected()
        fun onLocationServiceDisconnected()
        fun onLocationReceived(location: Location)
    }
}