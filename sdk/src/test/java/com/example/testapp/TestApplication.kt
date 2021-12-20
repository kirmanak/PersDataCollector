package com.example.testapp

import android.app.Application
import dagger.hilt.android.testing.CustomTestApplication
import org.robolectric.shadows.ShadowLog
import timber.log.Timber

open class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.Theme_AppCompat)
        ShadowLog.setupLogging()
        Timber.plant(Timber.DebugTree())
    }
}


@CustomTestApplication(TestApplication::class)
interface HiltTestApplication