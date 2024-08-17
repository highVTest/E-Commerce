package com.highv.ecommerce

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.scheduling.annotation.EnableScheduling

@EnableAspectJAutoProxy
@EnableScheduling
@SpringBootApplication
class ECommerceApplication

fun main(args: Array<String>) {
    runApplication<ECommerceApplication>(*args)
}
