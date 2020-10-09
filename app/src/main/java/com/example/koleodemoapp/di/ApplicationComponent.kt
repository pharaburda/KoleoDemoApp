package com.example.koleodemoapp.di

import com.example.koleodemoapp.ui.main.MainFragment
import dagger.Component

@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    fun inject(fragment: MainFragment)
}