package com.example.koleodemoapp.di

import com.example.koleodemoapp.ui.main.MainFragment
import dagger.Component
import javax.inject.Singleton

@Component(modules = [ApplicationModule::class])
@Singleton
interface ApplicationComponent {
    fun inject(fragment: MainFragment)
}