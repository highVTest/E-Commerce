package com.highv.ecommerce.domain.product.dto

import java.time.LocalDateTime

data class CreateProductRequest(
    val name: String,
    val description: String,
    val productImage: String,
    val createdAt: LocalDateTime,
    val shopId: Long,
    val categoryId: Long
)