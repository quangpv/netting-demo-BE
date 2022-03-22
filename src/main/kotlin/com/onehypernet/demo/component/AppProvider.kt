package com.onehypernet.demo.component

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.onehypernet.demo.component.security.JwtTokenProvider
import com.onehypernet.demo.datasource.ForexApi
import com.onehypernet.demo.datasource.WiseApi
import com.onehypernet.demo.guard.GuardInterceptor
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.keyvalue.core.KeyValueAdapter
import org.springframework.data.keyvalue.core.KeyValueOperations
import org.springframework.data.keyvalue.core.KeyValueTemplate
import org.springframework.data.map.MapKeyValueAdapter
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.multipart.support.StandardServletMultipartResolver
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


@Configuration
@EnableAutoConfiguration
@EnableWebMvc
@EnableScheduling
@ComponentScan(
    basePackages = ["com.onehypernet.demo.*"]
)
open class AppProvider : WebMvcConfigurer {
    @Autowired
    private lateinit var tokenProvider: JwtTokenProvider

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(GuardInterceptor(tokenProvider))
        super.addInterceptors(registry)
    }

    @Bean
    open fun objectMapperBuilder(): Jackson2ObjectMapperBuilder {
        val builder = Jackson2ObjectMapperBuilder()
        builder.modulesToInstall(KotlinModule())
        return builder
    }

    @Bean
    open fun multipartResolver(): StandardServletMultipartResolver {
        return StandardServletMultipartResolver()
    }

    @Bean
    open fun keyValueTemplate(): KeyValueOperations {
        return KeyValueTemplate(keyValueAdapter())
    }

    @Bean
    open fun keyValueAdapter(): KeyValueAdapter {
        return MapKeyValueAdapter(WeakHashMap::class.java)
    }

    @Bean
    open fun retrofitBuilder(interceptor: ForexApiKeyInterceptor): Retrofit.Builder {
        return Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Bean
    open fun forexApi(builder: Retrofit.Builder): ForexApi {
        return builder
            .baseUrl("https://api.polygon.io/v2/")
            .build()
            .create(ForexApi::class.java)
    }

    @Bean
    open fun wiseApi(builder: Retrofit.Builder): WiseApi {
        return builder
            .baseUrl("https://api.wise.com/v3/")
            .build()
            .create(WiseApi::class.java)
    }
}