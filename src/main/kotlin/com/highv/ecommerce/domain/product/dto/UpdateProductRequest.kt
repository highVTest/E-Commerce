package com.highv.ecommerce.domain.product.dto

import java.time.LocalDateTime

data class UpdateProductRequest(
    val name: String,
    val description: String,
    val productImage: String,
    val updatedAt: LocalDateTime,
    val isSoldOut: Boolean,
    val categoryId: Long,
)