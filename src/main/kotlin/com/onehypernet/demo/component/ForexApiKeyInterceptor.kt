package com.onehypernet.demo.component

import okhttp3.Interceptor
import okhttp3.Response
import org.springframework.stereotype.Component

@Component
class ForexApiKeyInterceptor(private val applicationProperties: ApplicationProperties) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        return chain.proceed(
            chain.request().newBuilder()
                .url(
                    original.url()
                        .newBuilder()
                        .addQueryParameter("apiKey", applicationProperties.forexApiKey)
                        .build()
                )
                .build()
        )
    }
}