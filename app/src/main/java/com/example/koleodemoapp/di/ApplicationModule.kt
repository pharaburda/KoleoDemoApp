package com.example.koleodemoapp.di

import com.example.koleodemoapp.repository.IRepository
import com.example.koleodemoapp.repository.RemoteRepository
import dagger.Module
import dagger.Provides

@Module
class ApplicationModule {
    @Provides
    fun provideRepository(): IRepository = RemoteRepository()
}