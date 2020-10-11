package com.example.koleodemoapp

import android.app.Application
import com.example.koleodemoapp.di.ApplicationModule
import com.example.koleodemoapp.di.DaggerApplicationComponent
import timber.log.Timber


class MainApplication : Application() {
    val appComponent = DaggerApplicationComponent
        .builder()
        .applicationModule(ApplicationModule(this))
        .build()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}