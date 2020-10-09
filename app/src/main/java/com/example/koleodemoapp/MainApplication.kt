package com.example.koleodemoapp

import android.app.Application
import com.example.koleodemoapp.di.DaggerApplicationComponent


class MainApplication : Application() {
    val appComponent = DaggerApplicationComponent.create()

}