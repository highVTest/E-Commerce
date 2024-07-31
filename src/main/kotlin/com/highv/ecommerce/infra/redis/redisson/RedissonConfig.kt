package com.highv.ecommerce.infra.redis.redisson

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

const val REDISSON_HOST_PREFIX = "redis://"

@Configuration
class RedissonConfig(

    @Value("\${spring.data.redis.host}")
    private val host: String,

    @Value("\${spring.data.redis.port}")
    private val port: Int,
){

    @Bean
    fun redissonClient(): RedissonClient {

        val config = Config()

        config.useSingleServer().setAddress("${REDISSON_HOST_PREFIX}${host}:${port}")

        return Redisson.create(config)
    }

}