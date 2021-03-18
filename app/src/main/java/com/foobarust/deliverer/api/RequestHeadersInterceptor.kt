package com.foobarust.deliverer.api

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by kevin on 3/1/21
 */

class RequestHeadersInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()

        return chain.proceed(request)
    }
}