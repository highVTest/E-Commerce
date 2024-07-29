package com.highv.ecommerce.domain.review.dto

data class CreateReviewRequest(
    val rate: Float,
    val content: String
)
