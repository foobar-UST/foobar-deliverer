package com.foobarust.deliverer.utils

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by kevin on 3/18/21
 */

private const val LPF_ALPHA = 0.0600f

/**
 * Get a sensor event flow of the target sensors
 * @param samplingDelay e.g. SENSOR_DELAY_UI
 */
fun SensorManager.orientationFlow(
    samplingDelay: Int
): Flow<OrientationResult> = callbackFlow {
    var gravity: FloatArray? = null
    var geomagnetic: FloatArray? = null

    val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            // Compute orientation result
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> gravity = applyLPF(event.values, gravity)
                Sensor.TYPE_MAGNETIC_FIELD -> geomagnetic = applyLPF(event.values, geomagnetic)
            }

            if (gravity != null && geomagnetic != null) {
                val r = FloatArray(9)
                val i = FloatArray(9)

                if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(r, orientation)
                    channel.offer(OrientationResult(
                        azimuth = toDegree(orientation[0]),
                        pitch = toDegree(orientation[1]),
                        roll = toDegree(orientation[2])
                    ))
                }
            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
    }

    registerListener(listener, getDefaultSensor(Sensor.TYPE_ACCELEROMETER), samplingDelay)
    registerListener(listener, getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), samplingDelay)

    awaitClose {
        unregisterListener(listener)
    }
}

private fun toDegree(input: Float): Float {
    return ((Math.toDegrees(input.toDouble()) + 360 ) % 360).toFloat()
}

private fun applyLPF(input: FloatArray, output: FloatArray?): FloatArray {
    if (output == null) return input
    val result = FloatArray(input.size)
    for (i in input.indices) {
        result[i] = output[i] + LPF_ALPHA * (input[i] - output[i])
    }
    return result
}

data class OrientationResult(
    val azimuth: Float,     // angle around the z-axis
    val pitch: Float,       // angle around the x-axis
    val roll: Float         // angle around the y-axis
)
