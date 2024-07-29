package com.highv.ecommerce.domain.auth.oauth.client.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class RestClientConfig {

    @Bean
    fun restClient() = RestClient.builder().build()

    }
