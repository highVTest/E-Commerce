package com.highv.ecommerce.domain.product.dto

data class CreateReviewRequest(
    val reviewerName: String,
    val rating: Int,
    val comment: String
)
