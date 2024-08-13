package com.highv.ecommerce

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories


@SpringBootApplication
class ECommerceApplication

fun main(args: Array<String>) {
    runApplication<ECommerceApplication>(*args)
}
