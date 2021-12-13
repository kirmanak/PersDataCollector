package com.example.testapp

import android.app.Application
import timber.log.Timber

class HostApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        Timber.v("onCreate() called")
    }
}