package com.highv.ecommerce.common.image.repository

import com.highv.ecommerce.common.image.dto.ImageUrlResponse
import com.highv.ecommerce.infra.s3.entity.Image
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component

@Configuration
@Component
class ImageRepository(
    private val mongoTemplate: MongoTemplate
)  {
    fun findByIdOrNull (id: Long): ImageUrlResponse {
        return mongoTemplate.findOne(
            Query().addCriteria(Criteria.where("_id").`is`(id)),
            ImageUrlResponse::class.java
        ) ?: throw RuntimeException("Image not found")
    }

    fun save(image: Image): Image {
        return mongoTemplate.save(image)
    }
}