package com.highv.ecommerce.domain.review.dto

data class ReviewRequest(
    val rate: Float,
    val content: String
)