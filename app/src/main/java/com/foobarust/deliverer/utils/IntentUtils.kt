package com.foobarust.deliverer.utils

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.foobarust.deliverer.BuildConfig

/**
 * Created by kevin on 4/4/21
 */

fun buildApplicationDetailSettingsIntent(): Intent {
    return Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
}