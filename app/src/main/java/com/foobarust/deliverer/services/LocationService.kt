package com.foobarust.deliverer.services

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.provider.ContactsContract.Directory.PACKAGE_NAME
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.foobarust.deliverer.R
import com.foobarust.deliverer.ui.auth.AuthActivity
import com.foobarust.deliverer.utils.cancelIfActive
import com.foobarust.deliverer.utils.locationFlow
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by kevin on 3/10/21
 */

private const val LOCATION_INTERVAL_SECOND = 5L

@AndroidEntryPoint
class LocationService : Service() {

    @Inject
    lateinit var localBroadcastManager: LocalBroadcastManager

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    //@Inject
    //lateinit var updateOrderLocationUseCase: UpdateOrderLocationUseCase

    private val locationRequest: LocationRequest by lazy {
        LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(LOCATION_INTERVAL_SECOND)
            fastestInterval = TimeUnit.SECONDS.toMillis(LOCATION_INTERVAL_SECOND / 2)
            //maxWaitTime = TimeUnit.MINUTES.toMillis(LOCATION_INTERVAL_SECOND)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private val localBinder: Binder by lazy { LocalBinder() }

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // Checks whether the bound activity has really gone away
    // (foreground service with notification created) or simply orientation change (no-op).
    private var configurationChange = false

    private var runningInForeground = false

    private var updateOrderLocationJob: Job? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Stop the service when tracking is canceled from notification
        val cancelTrackingFromNotification = intent.getBooleanExtra(
            EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION,
            false
        )

        if (cancelTrackingFromNotification) {
            unsubscribeLocationUpdates()
            stopSelf()
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        // Stop the foreground service when bind
        stopForeground(true)
        runningInForeground = false
        configurationChange = false
        return localBinder
    }

    override fun onRebind(intent: Intent?) {
        stopForeground(true)
        runningInForeground = false
        configurationChange = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        // MainActivity (client) leaves foreground, so service needs to become a foreground service
        // to maintain the 'while-in-use' label.
        // NOTE: If this method is called due to a configuration change in MainActivity,
        // we do nothing.
        if (!configurationChange) {
            startForeground(NOTIFICATION_ID, generateNotification())
            runningInForeground = true
        }

        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configurationChange = true
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun subscribeLocationUpdates(updateOrderId: String? = null) {
        updateOrderLocationJob.cancelIfActive()

        // Start the service
        startService(
            Intent(applicationContext, LocationService::class.java)
        )

        // Observe location updates
        updateOrderLocationJob = coroutineScope.launch {
            fusedLocationProviderClient.locationFlow(locationRequest).collect { currentLocation ->
                // Notify our Activity that a new location was received.
                Intent(ACTION_LOCATION_BROADCAST).apply {
                    putExtra(EXTRA_LOCATION, currentLocation)
                }.also {
                    localBroadcastManager.sendBroadcast(it)
                }

                // TODO: Updates notification if the service is running as a foreground service.
                if (runningInForeground) {
                    notificationManager.notify(
                        NOTIFICATION_ID,
                        generateNotification(currentLocation)
                    )
                }

                /*
                updateOrderId?.let {
                    val params = UpdateOrderLocationParameters(
                        orderId = updateOrderId,
                        geolocationPoint = GeolocationPoint(
                            latitude = currentLocation.latitude,
                            longitude = currentLocation.longitude
                        )
                    )
                    updateOrderLocationUseCase(params)
                } ?: emptyFlow()
                }.collect {
                    when (it) {
                        is Resource.Success -> Log.d(TAG, "Updated order location.")
                        is Resource.Error -> Log.d(TAG, "${it.message}")
                        is Resource.Loading -> Unit
                    }
                }

                 */
            }
        }
    }

    fun unsubscribeLocationUpdates() {
        updateOrderLocationJob.cancelIfActive()
    }

    private fun generateNotification(location: Location? = null): Notification {
        val activityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, AuthActivity::class.java),
            0
        )

        val servicePendingIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, LocationService::class.java).apply {
                putExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, true)
            },
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(
            applicationContext,
            getString(R.string.notification_channel_tracking_id)
        )

        return notificationBuilder
            .setContentTitle(getString(R.string.notification_tracking_title))
            //.setContentText(getString(R.string.notification_tracking_body))
            .setContentText("${location?.latitude}, ${location?.longitude}")
            .setSmallIcon(R.drawable.ic_restaurant)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(activityPendingIntent)
            .addAction(
                R.drawable.ic_launch,
                getString(R.string.notification_tracking_action_launch),
                activityPendingIntent
            )
            .addAction(
                R.drawable.ic_close,
                getString(R.string.notification_tracking_action_cancel),
                servicePendingIntent
            )
            .build()
    }

    inner class LocalBinder : Binder() {
        internal val service: LocationService
            get() = this@LocationService
    }

    companion object {
        private const val TAG = "LocationService"

        private const val NOTIFICATION_ID = 12345678

        private const val EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION =
            "$PACKAGE_NAME.extra.CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION"

        const val EXTRA_LOCATION = "$PACKAGE_NAME.extra.LOCATION"

        const val ACTION_LOCATION_BROADCAST =
            "$PACKAGE_NAME.action.FOREGROUND_ONLY_LOCATION_BROADCAST"
    }
}