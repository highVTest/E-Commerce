package com.highv.ecommerce.infra.redis.config

import com.highv.ecommerce.domain.product.dto.ProductResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Page
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig(
    @Value("\${spring.data.redis.host}")
    private val host: String,

    @Value("\${spring.data.redis.port}")
    private val port: Int,
) {

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(host, port)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val redisTemplate: RedisTemplate<String, Any> = RedisTemplate<String, Any>()
        redisTemplate.connectionFactory = redisConnectionFactory()

        // 일반적인 key:value의 경우 시리얼라이저
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = StringRedisSerializer()

        // // Hash를 사용할 경우 시리얼라이저
        // redisTemplate.hashKeySerializer = StringRedisSerializer()
        // redisTemplate.hashValueSerializer = StringRedisSerializer()
        //
        // // 모든 경우
        // redisTemplate.setDefaultSerializer(StringRedisSerializer())

        return redisTemplate
    }

    @Bean
    fun productRedisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Page<ProductResponse>> {
        val template = RedisTemplate<String, Page<ProductResponse>>()
        template.connectionFactory = redisConnectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = GenericJackson2JsonRedisSerializer()
        return template
    }
}