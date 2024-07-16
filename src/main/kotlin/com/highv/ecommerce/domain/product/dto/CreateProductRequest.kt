package com.highv.ecommerce.domain.product.dto

import java.time.LocalDateTime

data class CreateProductRequest (
    val name:String,
    val description:String,
    val price: Int,
    val productImage:String,
    val createdAt: LocalDateTime,
    val quantity:Int,
    val shopId:Long,
    val categoryId:Long,
)