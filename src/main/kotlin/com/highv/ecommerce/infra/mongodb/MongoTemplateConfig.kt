package com.highv.ecommerce.infra.mongodb

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory

@Configuration
@EnableConfigurationProperties(SearchMongoDBProperties::class)
class MongoTemplateConfig (
    private val searchMongoDBProperties: SearchMongoDBProperties,
) {
    @Bean(name = [SEARCH_MONGODB_CLIENT])
    fun searchMongoClient(): MongoClient =
        MongoClients.create(searchMongoDBProperties.createConnectionString())

    @Bean(name = [SEARCH_MONGODB_FACTORY])
    fun searchMongoFactory(): SimpleMongoClientDatabaseFactory =
        SimpleMongoClientDatabaseFactory(searchMongoClient(), searchMongoDBProperties.getDB())

    @Bean(name = [SEARCH_MONGODB_TEMPLATE])
    fun searchMongoTemplate(): MongoTemplate = MongoTemplate(searchMongoFactory())

    companion object {
        private const val SEARCH_MONGODB_FACTORY = "search-mongodb-factory"
        private const val SEARCH_MONGODB_TEMPLATE = "search-mongodb-template"
        private const val SEARCH_MONGODB_CLIENT = "search-mongodb-client"
    }
}