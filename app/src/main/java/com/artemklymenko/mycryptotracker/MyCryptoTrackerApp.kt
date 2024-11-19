package com.artemklymenko.mycryptotracker

import android.app.Application
import com.artemklymenko.mycryptotracker.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyCryptoTrackerApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyCryptoTrackerApp)
            androidLogger()

            modules(appModule)
        }
    }
}