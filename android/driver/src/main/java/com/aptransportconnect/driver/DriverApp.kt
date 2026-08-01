package com.aptransportconnect.driver

import android.app.Application
import com.aptransportconnect.driver.utils.Constants
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class DriverApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.DRIVER_TOPIC)
    }
}
