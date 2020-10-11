package com.example.koleodemoapp.di

import android.app.Application
import android.content.Context
import com.example.koleodemoapp.repository.IRepository
import com.example.koleodemoapp.repository.RemoteRepository
import com.example.koleodemoapp.services.KoleoService
import com.example.koleodemoapp.services.HeaderInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class ApplicationModule(private val application: Application) {
    @Provides
    fun provideRepository(service: KoleoService): IRepository = RemoteRepository(service)

    @Provides
    fun provideApplicationContext(): Context = application.applicationContext

    @Provides
    fun provideCache(context: Context): Cache =
        Cache(context.cacheDir, CACHE_SIZE)

    @Provides
    fun provideOkHttpClient(cache: Cache): OkHttpClient =
        OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides
    fun provideKoleoService(client: OkHttpClient): KoleoService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .addCallAdapterFactory(
                RxJava3CallAdapterFactory.create())
            .build()
            .create(KoleoService::class.java)
    }

    companion object {
        const val CACHE_SIZE: Long = 5 * 1024 * 1024
        const val BASE_URL = "https://koleo.pl/api/v2/main/"
    }

}