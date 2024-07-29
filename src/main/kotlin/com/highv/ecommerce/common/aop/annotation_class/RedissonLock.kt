package com.highv.ecommerce.common.aop.annotation_class

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
annotation class RedissonLock(

    val value: String, // Lock 이름

    val waitTime: Long = 2000L,  // Lock획득을 시도하는 최대 시간 (ms)

    val leaseTime: Long = 5000L,
)
