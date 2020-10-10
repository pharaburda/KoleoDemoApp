package com.example.koleodemoapp.di

import com.example.koleodemoapp.repository.IRepository
import com.example.koleodemoapp.repository.RemoteRepository
import com.example.koleodemoapp.services.KoleoService
import com.example.koleodemoapp.services.MyInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class ApplicationModule {
    @Provides
    fun provideRepository(service: KoleoService): IRepository = RemoteRepository(service)

    //@Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            //.cache(cache)
            .addInterceptor(MyInterceptor())
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    //@Singleton
    @Provides
    fun provideKoleoService(client: OkHttpClient): KoleoService {
        return Retrofit.Builder()
            .baseUrl("https://koleo.pl/api/v2/main/")
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .addCallAdapterFactory(
                RxJava3CallAdapterFactory.create())
            .build()
            .create(KoleoService::class.java)
    }

}