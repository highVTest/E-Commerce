package com.highv.ecommerce.infra.s3.entity


import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.MongoId


@Document(collection = "Image")
class Image(
    @MongoId
    val id: String? = null,

    @Field("imageUrl")
    var imageUrl: List<String>,

    @Field("imagename")
    var imageName: String,

    @Field("usage path") //신원확인
    var usagePath: String,

    @Field("use id") //신원의 Id = buyerId , sellerId, shopId, productId
    var useId: String? = null,

)