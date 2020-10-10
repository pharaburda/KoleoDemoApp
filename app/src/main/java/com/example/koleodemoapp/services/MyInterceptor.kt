package com.example.koleodemoapp.services

import okhttp3.Interceptor
import okhttp3.Response

class MyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("X-KOLEO-Version", "1")
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}