package com.foobarust.deliverer.utils

import android.Manifest
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by kevin on 3/18/21
 */

private const val TAG = "FusedLocationProvider"

@RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
fun FusedLocationProviderClient.locationFlow(
    locationRequest: LocationRequest
): Flow<Location> = callbackFlow {
    val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            result?.let {
                channel.offer(it.lastLocation)
            }
        }
    }

    requestLocationUpdates(
        locationRequest,
        callback,
        Looper.getMainLooper()
    ).addOnFailureListener { e ->
        channel.close(e)
    }

    awaitClose {
        removeLocationUpdates(callback)
    }
}