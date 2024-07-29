package com.highv.ecommerce.infra.redis.redisson

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

const val REDISSON_HOST_PREFIX = "redis://"

@Configuration
class RedissonConfig {

    @Bean
    fun redissonClient(): RedissonClient {

        val config = Config()

        config.useSingleServer().setAddress("${REDISSON_HOST_PREFIX}localhost:6379")

        return Redisson.create(config)
    }

}