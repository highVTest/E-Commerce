package com.highv.ecommerce.domain.product.dto

data class CreateReviewRequest(
    val rate: Float,
    val content: String
)
