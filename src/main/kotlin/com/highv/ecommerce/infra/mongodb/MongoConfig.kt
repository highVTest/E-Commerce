package com.highv.ecommerce.infra.mongodb

import com.mongodb.ConnectionString
//import com.mongodb.MongoClientSettings
//import com.mongodb.client.MongoClients
//import org.springframework.context.annotation.Configuration
//import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
//import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
//
//@Configuration
//@EnableMongoRepositories
//class MongoConfig : AbstractMongoClientConfiguration() {
//
//    override fun getDatabaseName(): String {
//        return "High-v"
//    }
//
//    override fun mongoClient(): com.mongodb.client.MongoClient {
//        val connectionString = ConnectionString("mongodb+srv://chadkim1021:x-SQAQ7vbUJEe38@cluster0807.e7xiz.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0807")
//
//        val mongoClientSettings = MongoClientSettings
//            .builder()
//            .applyConnectionString(connectionString)
//            .build()
//        return MongoClients.create(mongoClientSettings)
//    }
//
//}