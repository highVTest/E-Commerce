package com.highv.ecommerce.domain.product.dto

import java.time.LocalDateTime

data class UpdateProductRequest (
    val name: String,
    val description: String,
    val price: Int,
    val productImage: String,
    val updatedAt: LocalDateTime,
    val quantity: Int,
    val isSoldOut: Boolean,
    val categoryId: Long,
)