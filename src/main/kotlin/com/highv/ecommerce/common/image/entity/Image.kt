package com.highv.ecommerce.infra.s3.entity


import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.MongoId


@Document(collection = "Image")
data class Image(
    @MongoId
    val id: String? = null,

    var imageUrl: List<String>,

    var usagePath: String, //신원확인

//    var useId: Long, //신원의 Id = buyerId , sellerId, shopId, productId

)