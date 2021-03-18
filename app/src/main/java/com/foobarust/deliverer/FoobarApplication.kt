package com.foobarust.deliverer

import android.app.Application
import android.app.NotificationManager
import androidx.core.app.NotificationManagerCompat
import com.foobarust.deliverer.utils.createNotificationChannel
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Created by kevin on 2/17/21
 */

@HiltAndroidApp
class FoobarApplication : Application() {

    @Inject
    lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        // Default channel
        notificationManager.createNotificationChannel(
            channelId = getString(R.string.notification_channel_default_id),
            channelName = getString(R.string.notification_channel_default_name)
        )

        // Upload channel
        notificationManager.createNotificationChannel(
            channelId = getString(R.string.notification_channel_upload_id),
            channelName = getString(R.string.notification_channel_upload_name)
        )

        // Location tracking channel
        notificationManager.createNotificationChannel(
            channelId = getString(R.string.notification_channel_tracking_id),
            channelName = getString(R.string.notification_channel_tracking_name),
            importance = NotificationManagerCompat.IMPORTANCE_LOW
        )
    }
}