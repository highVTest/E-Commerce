package com.highv.ecommerce.domain.product.dto

data class UpdateProductRequest(
    val name: String,
    val description: String,
    val productImage: String,
    val isSoldOut: Boolean,
    val categoryId: Long,
)