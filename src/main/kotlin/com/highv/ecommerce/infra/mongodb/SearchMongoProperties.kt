package com.highv.ecommerce.infra.mongodb


import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration


@Configuration
data class SearchMongoDBProperties(
    @Value("\${spring.data.mongodb.search.host}")
    private val host: String,

    @Value("\${spring.data.mongodb.search.user}")
    private val user: String,

    @Value("\${spring.data.mongodb.search.password}")
    private val password: String,

    @Value("\${spring.data.mongodb.search.db}")
    private val db: String
) {
    fun createConnectionString(): String =
        "mongodb+srv://$user:$password@$host/?retryWrites=true&w=majority&appName=$db&authSource=admin"

    fun getDB(): String = db

}
