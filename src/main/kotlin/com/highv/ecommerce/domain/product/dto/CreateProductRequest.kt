package com.highv.ecommerce.domain.product.dto

data class CreateProductRequest(
    val name: String,
    val description: String,
    val productImage: String,
    val categoryId: Long
)