package com.example.koleodemoapp.services

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header(HEADER_VERSION, "1")
        val request = requestBuilder.build()
        return chain.proceed(request)
    }

    companion object {
        const val HEADER_VERSION = "X-KOLEO-Version"
    }
}