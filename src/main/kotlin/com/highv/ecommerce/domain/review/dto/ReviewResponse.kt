package com.highv.ecommerce.domain.review.dto

data class ReviewResponse(
    val id: Long,
    val rate: Float,
    val content: String,

)
