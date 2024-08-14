package com.highv.ecommerce.infra.s3.entity


import com.highv.ecommerce.common.image.entity.UsagePath
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.MongoId


@Document(collection = "Image")
data class Image(
    @MongoId
    var id: String? = null,

    var imageUrl: List<String>,

    var usagePath: UsagePath, //신원확인

//    var useId: Long, //신원의 Id = buyerId , sellerId, shopId, productId

)